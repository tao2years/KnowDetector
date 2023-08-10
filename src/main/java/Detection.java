import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import graph.Edge;
import graph.Graph;
import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Detection {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String[] physicalList = new String[]{"temperature","humidity", "motion", "leakage", "location", "smoke", "luminance"};

    private static String escapeForCSV(String value) {
        // If the content contains a comma, it should be enclosed in double quotation marks.
        if (value.contains("\"") || value.contains(",")) {
            value = "\"" + value.replaceAll("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void deviceLoopDetection(Graph<Object, Object> AppGraph, ArrayList<ResultModel> results) {
        String pattern = "DeviceLoop";
        List<List<String>> cycList = AppGraph.findCycles();
        Collection<Edge> edges = AppGraph.getEdges();
        cycList.forEach(temp ->{
            if (temp.size() == 2 ){
                String source = temp.get(0);
                String target = temp.get(1);
                String[] nodesRelation = identifyCycleType(source, target, edges);
                AppInfo source2Target = AppInfo.fromString(nodesRelation[0]);
                AppInfo target2Source = AppInfo.fromString(nodesRelation[1]);
                // If the relationship between two edges is "control/implied" for both, it is considered as a device loop
                Set<String> relatedFiles = new HashSet<>();
                if (source2Target.getFileName() != null)
                    relatedFiles.add(source2Target.getFileName());
                if (target2Source.getFileName() != null)
                    relatedFiles.add(target2Source.getFileName());
                if (identifyEdge(source2Target.getRelation()) && identifyEdge(target2Source.getRelation())){
                    ArrayList<String> relatedEdges = new ArrayList<>();
                    relatedEdges.add(source.toString());
                    relatedEdges.add(target.toString());
                    results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                }
            }else {
                int length = temp.size();
                Set<String> relatedFiles = new HashSet<>();
                ArrayList<String> relatedEdges = new ArrayList<>();
                if (length > 2){
                    for (int i=0; i<length-1; i++){
                        String source = temp.get(i);
                        String target = temp.get(i+1);
                        Set<Edge> STegdes = AppGraph.getEdgesBasedOnNodes(source, target);
                        STegdes.forEach(edge -> {
                            if (edge.getName().contains("control")||edge.getName().contains("implied")){
                                AppInfo source2Target = AppInfo.fromString(edge.getName());
                                relatedFiles.add(source2Target.getFileName());
                            }
                        });
                        Set<Edge> ST2egdes = AppGraph.getEdgesBasedOnNodes(target, source);
                        ST2egdes.forEach(edge -> {
                            if (edge.getName().contains("control")||edge.getName().contains("implied")){
                                AppInfo source2Target = AppInfo.fromString(edge.getName());
                                relatedFiles.add(source2Target.getFileName());
                            }
                        });
                    }
                    Set<Edge> STegdes = AppGraph.getEdgesBasedOnNodes(temp.get(length-1), temp.get(0));
                    STegdes.forEach(edge -> {
                        if (edge.getName().contains("control")||edge.getName().contains("implied")){
                            AppInfo source2Target = AppInfo.fromString(edge.getName());
                            relatedFiles.add(source2Target.getFileName());
                        }
                    });
                    Set<Edge> ST2egdes = AppGraph.getEdgesBasedOnNodes(temp.get(0),temp.get(length-1));
                    ST2egdes.forEach(edge -> {
                        if (edge.getName().contains("control")||edge.getName().contains("implied")){
                            AppInfo source2Target = AppInfo.fromString(edge.getName());
                            relatedFiles.add(source2Target.getFileName());
                        }
                    });
                    if (relatedFiles.size()>0){
                        temp.forEach(node->{
                            relatedEdges.add(node);
                        });
                        results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                    }
                }
            }
        });
    }

    private Boolean identifyEdge(String temp){
        return temp!=null && (temp.contains("control") || temp.contains("implied"));
    }

    public void blockedActionDetection(Graph<Object, Object> AppGraph, ArrayList<ResultModel> results){
        String pattern = "BlockedAction";
        Collection<Edge> edges = AppGraph.getEdges();
        List<ExclusiveNodePair> exclusiveList = new ArrayList<>();
        edges.forEach(edge -> {
            if (edge.getName().equals("exclusive")){
                exclusiveList.add(new ExclusiveNodePair(edge.getSource(), edge.getTarget()));
            }
        });
        // Show exclusive nodes
        exclusiveList.forEach(nodePair ->{
            String sourceId = nodePair.getSource();
            String targetId = nodePair.getTarget();
            Set<Edge> nodeSet = AppGraph.getEdgesBasedOnNodes(sourceId, targetId);
            // A -[control]-> B && A -[exclusive]-> B
            boolean multiExFlag = true;
            for (Edge e : nodeSet){
                AppInfo temp = AppInfo.fromString(e.getName());
                if (temp.getRelation().equals("control")){
                    multiExFlag = false;
                    results.add(new ResultModel(pattern, temp.getFileName(), nodePair.toString()));
                }
            }
            // A -> B && C -> D && B -[exclusive]-> D
            if (multiExFlag){
                Collection<Edge> BInEdges = AppGraph.inEdges(sourceId);
                Collection<Edge> DInEdges = AppGraph.inEdges(targetId);
                BInEdges.forEach(inB->{
                    DInEdges.forEach(inD->{
                        AppInfo source2Target = AppInfo.fromString(inB.getName());
                        AppInfo target2Source = AppInfo.fromString(inD.getName());
                        ArrayList<String> relatedFiles = new ArrayList<>();
                        relatedFiles.add(source2Target.getFileName());
                        relatedFiles.add(target2Source.getFileName());
                        ArrayList<String> relatedEdges = new ArrayList<>();
                        relatedEdges.add(inB.toString());
                        relatedEdges.add(inD.toString());
                        if (source2Target.getRelation().equals("control") &&
                                target2Source.getRelation().equals("control")){
                            results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                        }
                    });
                });
            }
        });
    }

    private void redundancyDetection(Graph<Object, Object> AppGraph, ArrayList<ResultModel> results){
        String pattern = "Redundancy";
        Collection<Edge> edges = AppGraph.getEdges();
        ArrayList<Edge> edgeList = new ArrayList<>(edges);
        ArrayList<String> phyList = new ArrayList<>(Arrays.asList(physicalList));
        if (edgeList.size() > 1){
            for (int l=0; l<edgeList.size(); l++){
                for (int r=l+1; r<edgeList.size(); r++){
                    Edge L = edgeList.get(l);
                    Edge R = edgeList.get(r);
                    AppInfo source2Target = AppInfo.fromString(L.getName());
                    AppInfo target2Source = AppInfo.fromString(R.getName());
                    ArrayList<String> relatedFiles = new ArrayList<>();
                    relatedFiles.add(source2Target.getFileName());
                    relatedFiles.add(target2Source.getFileName());
                    ArrayList<String> relatedEdges = new ArrayList<>();
                    if (!phyList.contains(L.getTarget()) && !phyList.contains(R.getTarget()) && L.getName().contains("fileName") && R.getName().contains("fileName")){
                        if (L.getSource().equals(R.getSource()) && L.getTarget().equals(R.getTarget()) && !L.getName().equals(R.getName())){
                            relatedEdges.add(L.toString());
                            relatedEdges.add(R.toString());
                            results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                        } else if (L.getTarget().equals(R.getTarget()) && !L.getName().equals(R.getName())){
                            Set<Edge> re_Edges = AppGraph.getEdgesBasedOnNodes(L.getSource(), R.getSource());
                            re_Edges.forEach(e->{
                                if (e.getName().contains("implied")){
                                    relatedEdges.add(L.toString());
                                    relatedEdges.add(R.toString());
                                    results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                                }
                            });
                            Set<Edge> re2_Edges = AppGraph.getEdgesBasedOnNodes(R.getSource(), L.getSource());
                            re2_Edges.forEach(e->{
                                if (e.getName().contains("implied")){
                                    relatedEdges.add(L.toString());
                                    relatedEdges.add(R.toString());
                                    results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                                }
                            });
                        } else if (L.getSource().equals(R.getSource()) && !L.getName().equals(R.getName())){
                            Set<Edge> re_Edges = AppGraph.getEdgesBasedOnNodes(L.getTarget(), R.getTarget());
                            re_Edges.forEach(e->{
                                if (e.getName().contains("implied")){
                                    relatedEdges.add(L.toString());
                                    relatedEdges.add(R.toString());
                                    results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                                }
                            });
                            Set<Edge> re2_Edges = AppGraph.getEdgesBasedOnNodes(R.getTarget(), L.getTarget());
                            re2_Edges.forEach(e->{
                                if (e.getName().contains("implied")){
                                    relatedEdges.add(L.toString());
                                    relatedEdges.add(R.toString());
                                    results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                                }
                            });
                        }else {
                            Set<Edge> S_Edges = AppGraph.getEdgesBasedOnNodes(L.getSource(), R.getSource());
                            Set<Edge> T_Edges = AppGraph.getEdgesBasedOnNodes(L.getTarget(), R.getTarget());
                            AtomicBoolean flagS = new AtomicBoolean(false);
                            AtomicBoolean flagT = new AtomicBoolean(false);
                            S_Edges.forEach(e->{
                                if (e.getName().contains("implied"))
                                    flagS.set(true);
                            });
                            T_Edges.forEach(e->{
                                if (e.getName().contains("implied"))
                                    flagT.set(true);
                            });
                            Set<Edge> S2_Edges = AppGraph.getEdgesBasedOnNodes(R.getSource(), L.getSource());
                            Set<Edge> T2_Edges = AppGraph.getEdgesBasedOnNodes(R.getTarget(), L.getTarget());
                            AtomicBoolean flag2S = new AtomicBoolean(false);
                            AtomicBoolean flag2T = new AtomicBoolean(false);
                            S2_Edges.forEach(e->{
                                if (e.getName().contains("implied"))
                                    flag2S.set(true);
                            });
                            T2_Edges.forEach(e->{
                                if (e.getName().contains("implied"))
                                    flag2T.set(true);
                            });
                            if ((flagS.get() && flagT.get())||(flag2S.get() && flag2T.get())){
                                relatedEdges.add(L.toString());
                                relatedEdges.add(R.toString());
                                results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                            }
                        }
                    }
                }
            }
        }
    }

    private void environmentLoopDetection(Map<String, Set<DataPair>> relationMap, Graph<Object, Object> AppGraph, ArrayList<ResultModel> results){
        String pattern = "EnvironmentLoop";
        relationMap.forEach((key, pair)->{
            pair.forEach(temp->{
                String leftNode = temp.getLeftEdge().getSource();
                String rightNode = temp.getRightEdge().getSource();
                Collection<Edge> inL = AppGraph.inEdges(leftNode);
                Collection<Edge> inB = AppGraph.inEdges(rightNode);
                inL.forEach(L->{
                    String leftCond = L.getSource().contains("cond.")? L.getSource():null;
                    if (leftCond != null){
                        inB.forEach(R->{
                            String rightCond = R.getSource().contains("cond.")? R.getSource():null;
                            if (rightCond != null){
                                boolean flag = identifyCond(leftCond, rightCond);
                                if (flag){
                                    AppInfo source2Target = AppInfo.fromString(L.getName());
                                    AppInfo target2Source = AppInfo.fromString(R.getName());
                                    ArrayList<String> relatedFiles = new ArrayList<>();
                                    relatedFiles.add(source2Target.getFileName());
                                    relatedFiles.add(target2Source.getFileName());
                                    ArrayList<String> relatedEdges = new ArrayList<>();
                                    relatedEdges.add(L.toString());
                                    relatedEdges.add(R.toString());
                                    results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                                }
                            }
                        });
                    }
                });
            });
        });
    }


    private void deviceConflictDetection(Graph<Object, Object> AppGraph, Map<String, Set<String>> deviceServiceList, ArrayList<ResultModel> results){
        String pattern = "DeviceConflict";
        deviceServiceList.forEach((key, value)->{
            // Identify values
            if (value.size() > 1){
                List<String> deviceValueList = new ArrayList<>(value);
                for (int i=0; i< deviceValueList.size();i++){
                    for (int j=i+1; j< deviceValueList.size(); j++){
                        String leftNode = key + "_" + deviceValueList.get(i);
                        String rightNode = key + "_" + deviceValueList.get(j);

                        Collection<Edge> BInEdges = AppGraph.inEdges(leftNode);
                        Collection<Edge> DInEdges = AppGraph.inEdges(rightNode);

                        BInEdges.forEach(inB->{
                            DInEdges.forEach(inD->{
                                if (AppInfo.fromString(inB.getName()).getRelation().equals("control") &&
                                        AppInfo.fromString(inD.getName()).getRelation().equals("control")){
                                    AppInfo source2Target = AppInfo.fromString(inB.getName());
                                    AppInfo target2Source = AppInfo.fromString(inD.getName());
                                    if (!identifySameDevice(inB, inD)){
                                        if (!source2Target.getFileName().equals(target2Source.getFileName())){
                                            ArrayList<String> relatedFiles = new ArrayList<>();
                                            relatedFiles.add(source2Target.getFileName());
                                            relatedFiles.add(target2Source.getFileName());
                                            ArrayList<String> relatedEdges = new ArrayList<>();
                                            relatedEdges.add(inB.toString());
                                            relatedEdges.add(inD.toString());
                                            results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
                                        }
                                    }
                                }
                            });
                        });
                    }
                }
            }
        });
    }

    private Boolean identifySameDevice(Edge e1, Edge e2){
        if (e1.getTarget().contains("_") && e1.getSource().contains("_") && e2.getTarget().contains("_") && e2.getSource().contains("_")){
            String[] source1 = e1.getSource().split("_");
            String[] target1 = e1.getTarget().split("_");
            String[] source2 = e1.getSource().split("_");
            String[] target2 = e1.getTarget().split("_");
            boolean flag = true;
            for (int i=0;i<3;i++){
                if (!source1[i].equals(target1[i]) || !source2[i].equals(target2[i]) || !source1[i].equals(source2[i]))
                    flag=false;
            }
            return flag;
        }
        return false;
    }

    private Boolean identifyCond(String left, String right){
        if (left.contains("year")||right.contains("year"))
            return false;
        Map<String, String> oppositeOperators = new HashMap<>();
        oppositeOperators.put(">", "<");
        oppositeOperators.put("<", ">");
        oppositeOperators.put(">=", "<=");
        oppositeOperators.put("<=", ">=");


        String[] leftArr = left.split("\\|");
        String[] rightArr = right.split("\\|");
        String leftParam = leftArr[leftArr.length-1];
        String rightParam = rightArr[rightArr.length-1];
        JSONObject LjsonObject = JSON.parseObject(leftParam);
        String L_op = LjsonObject.get("operator").toString();

        JSONObject RjsonObject = JSON.parseObject(rightParam);
        String R_op = RjsonObject.get("operator").toString();

        return oppositeOperators.containsKey(L_op) && oppositeOperators.get(L_op).equals(R_op);
    }

    private Map<String, Set<DataPair>> environmentConflictDetection(Graph<Object, Object> AppGraph, ArrayList<ResultModel> results){
        String pattern = "EnvironmentConflict";
        ArrayList<String> physicalArray = new ArrayList<>(Arrays.asList(physicalList));
        // For each physical channels
        Map<String, Set<DataPair>> relationMap = new HashMap<>();
        physicalArray.forEach(physical->{
            Set<DataPair> result = new HashSet<>();
            Collection<Edge> phyInEdges = AppGraph.inEdges(physical);
            phyInEdges.forEach(left->{
                phyInEdges.forEach(right ->{
                    if (identifyUpDownRelation(left, right)){
                        DataPair dataPair = new DataPair(left, right);
                        result.add(dataPair);
                    }
                });
                if (result.size()>0){
                    relationMap.put(physical, result);
                }
            });
        });
        // Handle result
        relationMap.forEach((key, pair)->{
            pair.forEach(temp->{
                AppInfo source2Target = AppInfo.fromString(temp.getLeftEdge().getName());
                AppInfo target2Source = AppInfo.fromString(temp.getRightEdge().getName());
                ArrayList<String> relatedFiles = new ArrayList<>();
                relatedFiles.add(source2Target.getFileName());
                relatedFiles.add(target2Source.getFileName());
                ArrayList<String> relatedEdges = new ArrayList<>();
                relatedEdges.add(temp.getLeftEdge().toString());
                relatedEdges.add(temp.getRightEdge().toString());
                results.add(new ResultModel(pattern, relatedFiles.toString(), relatedEdges.toString()));
            });
        });
        return relationMap;
    }


    private Boolean identifyUpDownRelation (Edge left, Edge right){
        return (left.getName().contains("hasDecreased") && right.getName().contains("hasIncreased")) ||
                (right.getName().contains("hasDecreased") && left.getName().contains("hasIncreased"));
    }

    private String[] identifyCycleType (String source, String target, Collection<Edge> edges) {
        String[] results = new String[2];
        for (Edge e: edges){
            if (e.getSource().equals(source) && e.getTarget().equals(target)){
                results[0] = e.getName();
            } else if (e.getSource().equals(target) && e.getTarget().equals(source)){
                results[1] = e.getName();
            }
        }
        return results;
    }

    private ArrayList<ResultModel> detectAndRecord (Graph<Object, Object> AppGraph, Map<String, Set<String>> deviceServiceList){
        ArrayList<ResultModel> results = new ArrayList<>();
        deviceLoopDetection(AppGraph, results);
        blockedActionDetection(AppGraph, results);
        deviceConflictDetection(AppGraph, deviceServiceList, results);
        Map<String, Set<DataPair>> relationMap = environmentConflictDetection(AppGraph, results);
        environmentLoopDetection(relationMap, AppGraph, results);
        redundancyDetection(AppGraph,results);
        return results;
    }

    private void saveResult2Csv(ArrayList<ResultModel> results, String filePath) {
        String file = filePath + "/result.csv";
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Pattern,relatedFiles,relatedNodesAndEdges");
            writer.append(System.lineSeparator());
            Set<String> uniqueLine = new HashSet<>();
            for (ResultModel mm : results) {
                if (!uniqueLine.contains(mm.toString())){
                    writer.append(escapeForCSV(mm.getPattern()));
                    writer.append(",");
                    writer.append(escapeForCSV(mm.getRelatedFiles()));
                    writer.append(",");
                    writer.append(escapeForCSV(mm.getRelatedEdges()));
                    writer.append(System.lineSeparator());
                    uniqueLine.add(mm.toString());
                }
            }
            writer.flush();
            LOGGER.info("Save result successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
//        if (args.length < 2){
//            LOGGER.info("Please provide correct arguments");
//        }
//        String testPath = args[0];
//        String savePath = args[1];
        String testPath = "src/main/resources/testFiles/3BlockedAction1R";
        String savePath = "src/main/resources/results";
        ConstructGraph cg = new ConstructGraph();
        GraphMapPair pair = cg.getAppGraph(testPath);
        Graph<Object, Object> AppGraph = pair.getUserGraph();
        Map<String, Set<String>> deviceServiceList = pair.getMap();
        Detection dt = new Detection();
        ArrayList<ResultModel> results = dt.detectAndRecord(AppGraph, deviceServiceList);
//        cg.showGraph(AppGraph);
        dt.saveResult2Csv(results, savePath);
    }
}
