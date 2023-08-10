import com.fasterxml.jackson.databind.ObjectMapper;
import graph.Edge;
import graph.Graph;
import model.AppInfo;
import model.ConditionNode;
import model.GraphMapPair;
import model.ServiceNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parseFile.*;
import util.MapTool;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ConstructGraph {
    private static final Logger LOGGER = LogManager.getLogger();
    private ObjectMapper objectMapper = MapTool.getObjectMapper();

    public GraphMapPair fileParse (String targetPath, List<CSVRecord> modelRecords, List<CSVRecord> relationRecords){
        File dir = new File(targetPath);
        if (!dir.exists()){
            LOGGER.warn("File does not exist" + dir);
            return null;
        }
        File[] userFiles = dir.listFiles();
        return constructGraph4User(dir.getName(), userFiles, modelRecords, relationRecords);
    }


    private GraphMapPair constructGraph4User(String userName, File[] userFiles, List<CSVRecord> modelRecords, List<CSVRecord> relationRecords){
        LOGGER.info(String.format("===== Start processing Apps of %s, %s files need to be processed =====", userName, userFiles.length));
        Graph<Object, Object> userGraph = new Graph<>(true,true,false);
        Map<String, Set<String>> deviceServiceList = new HashMap<>();
        Arrays.stream(userFiles)
//                .parallel()
                .forEach(file -> {
                    String fileName = file.getName();
                    try {
                        String content = new String(Files.readAllBytes(file.toPath()));
                        App app = objectMapper.readValue(content, App.class);
                        addAppIntoGraph(app, fileName, userGraph, deviceServiceList, modelRecords, relationRecords);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        GraphMapPair pair = new GraphMapPair(userGraph, deviceServiceList);
        return pair;
    }

    private void addAppIntoGraph(App app, String fileName, Graph<Object, Object> userGraph, Map<String, Set<String>> deviceServiceList, List<CSVRecord> modelRecords, List<CSVRecord> relationRecords) {
        String appId = app.getId();
        AppInfo appInfo = new AppInfo(appId, fileName);

        // node list
        ArrayList<ConditionNode> conditionList = new ArrayList<>();
        ArrayList<ServiceNode> eventList = new ArrayList<>();
        ArrayList<ServiceNode> actionList = new ArrayList<>();

        // app parse
        Trigger trigger = app.getTrigger();
        List<RuntimeCondition> conditions = trigger.getConditions();
        if (conditions.size() > 0) {
            handleCondition(appInfo, conditions.get(0), conditionList, userGraph, modelRecords, relationRecords);
        }
        List<RuntimeEvents> events = trigger.getEvents();
        if (events.size() > 0){
            handleEvent(appInfo, events.get(0), eventList, userGraph, modelRecords, relationRecords);
        }
        List<Action> actions = app.getActions();
        if (actions.size() > 0) {
            Action action = app.getActions().get(0);
            List<RuntimeActions> inActions = action.getActions();
            Delay delay = action.getDelay();
            handleAction(appInfo, inActions, delay, actionList, userGraph, modelRecords, relationRecords);
        }

        // add nodes and edges - single connection (can be replaced by multi-connection)
        singleConnectNodes(appInfo, userGraph,  deviceServiceList, conditionList, eventList, actionList, relationRecords);

    }


    private void handleCondition(AppInfo appInfo, RuntimeCondition condition, ArrayList<ConditionNode> conditionList, Graph<Object, Object> userGraph, List<CSVRecord> modelRecords, List<CSVRecord> relationRecords) {
        String condId = condition.getCondId();
        String physical = condition.getPhysical();
        String params = condition.getParams();
        ConditionNode node = new ConditionNode(condId, physical, params);
        conditionList.add(node);

    }


    private void handleEvent(AppInfo appInfo, RuntimeEvents event, ArrayList<ServiceNode> eventList, Graph<Object, Object> userGraph, List<CSVRecord> modelRecords, List<CSVRecord> relationRecords) {
        String devType = event.getDevType();
        String prodId = event.getProdId();
        String deviceId = event.getDeviceId();
        RuntimeParams params = event.getParams();
        String capabilityId = params.getCapabilityId();
        String command = params.getCommand();
        String value = params.getValue();

        String serviceId = searchService(devType, prodId, capabilityId, command, value, modelRecords);

        if (serviceId != null) {
            ServiceNode node = new ServiceNode(devType, prodId, deviceId, capabilityId, command, value, serviceId, "0");
            eventList.add(node);
        } else{
            LOGGER.warn(String.format("[Unlisted device in Event] File %s, devType %s, prodId: %s, capabilityId: %s, commandId: %s, value: %s"
                    ,appInfo.getFileName(), devType, prodId, capabilityId, command, value));
        }
    }


    private void handleAction(AppInfo appInfo, List<RuntimeActions> actions, Delay delay, ArrayList<ServiceNode> actionList,  Graph<Object, Object> userGraph, List<CSVRecord> modelRecords, List<CSVRecord> relationRecords) {
        String delaySync = delay.getDelaySync();
        String _Delay = "0";
        if (delaySync.equals("true")){
            _Delay = delay.getTime();
        }

        for (RuntimeActions action: actions) {
            String devType = action.getDevType();
            String prodId = action.getProdId();
            String deviceId = action.getDeviceId();
            RuntimeParams params = action.getParams();
            String capabilityId = params.getCapabilityId();
            String command = params.getCommand();
            String value = params.getValue();

            String[] results = searchService4Action(devType, prodId, capabilityId, command, value, modelRecords);

            if (results[0] != null) {
                ServiceNode node = new ServiceNode(devType, prodId, deviceId, capabilityId, command, value, results[0], _Delay);
                // Add physical nodes and influence
                if (results[1].length() > 0){
                    String targetNodeId = results[1];
                    String sourceNodeId = devType + "|" + prodId + "|" + deviceId + "|" + results[0];
                    appInfo.setRelation(results[2]);
                    userGraph.setEdge(sourceNodeId, targetNodeId, results[2], appInfo.toString());
                }
                actionList.add(node);
            } else {
                LOGGER.warn(String.format("[Unlisted device in Action] File %s, devType %s, prodId: %s, capabilityId: %s, commandId: %s, value: %s"
                        ,appInfo.getFileName() , devType, prodId, capabilityId, command, value));
            }
        }

    }

    private String[] searchService4Action(String devType, String prodId, String capabilityId, String command, String value, List<CSVRecord> modelRecords){
        // result[0] - serviceId
        // result[1] - influencedChannels
        // result[2] - influencedType
        String[] results = new String[3];
        results[0] = null;
        for (CSVRecord record : modelRecords) {
            if (devType.equals(record.get("devType").replace("\"","")) &&
                    prodId.equals(record.get("prodId").replace("\"","")) &&
                    capabilityId.equals(record.get("capabilityId")) && command.equals(record.get("commandId"))) {
                // identify value
                if (value.contains("aiting")) {
                    results[0] = record.get("serviceId");
                    results[1] = record.get("influencedChannels");
                    results[2] = record.get("influenceType");
                    break;
                } else if (value.equals(record.get("value"))){
                    results[0] = record.get("serviceId");
                    results[1] = record.get("influencedChannels");
                    results[2] = record.get("influenceType");
                    break;
                }else if (record.get("valueType").equals("range")){
                    results[0] = record.get("serviceId");
                    results[1] = record.get("influencedChannels");
                    results[2] = record.get("influenceType");
                    break;
                }
            }
        }
        return results;
    }

    private String searchService(String devType, String prodId, String capabilityId, String command, String value, List<CSVRecord> modelRecords){
        String serviceId = null;
        for (CSVRecord record : modelRecords) {
            if (devType.equals(record.get("devType").replace("\"","")) &&
                    prodId.equals(record.get("prodId").replace("\"","")) &&
                    capabilityId.equals(record.get("capabilityId")) && command.equals(record.get("commandId"))) {
                // identify value
                if (value.contains("aiting")) {
                    serviceId = record.get("serviceId");
                    break;
                } else if (value.equals(record.get("value"))){
                    serviceId = record.get("serviceId");
                    break;
                }else if (record.get("valueType").equals("range")){
                    serviceId = record.get("serviceId");
                    break;
                }
            }
        }
        return serviceId;
    }

    private void singleConnectNodes(AppInfo appInfo, Graph<Object, Object> userGraph, Map<String, Set<String>> deviceServiceList, ArrayList<ConditionNode> conditionList, ArrayList<ServiceNode> eventList, ArrayList<ServiceNode> actionList, List<CSVRecord> relationRecords) {
        ArrayList<ServiceNode> allEventActionNodes = new ArrayList<>();
        for (ServiceNode eventNode : eventList)
            allEventActionNodes.add(eventNode);
        for (ServiceNode actionNode : actionList)
            allEventActionNodes.add(actionNode);
        if (conditionList.isEmpty()) {
            if (allEventActionNodes.size() <= 1) return;
            connectLists(appInfo, userGraph,deviceServiceList, allEventActionNodes);
        }else {
            // source node - condition
            ConditionNode conditionNode = conditionList.get(0);
            String sourceNodeId = conditionNode.getCondId() + "|" + conditionNode.getPhysicalChannel() + "|" + conditionNode.getParams();
            // first target node
            if (allEventActionNodes.size() > 0) {
                ServiceNode target = allEventActionNodes.get(0);
                String targetNodeId = target.getDevType() + "|" + target.getProdId() + "|" + target.getDeviceId() + "|" +
                        target.getServiceId();
                appInfo.setRelation("control");
                userGraph.setEdge(sourceNodeId, targetNodeId, "control", appInfo.toString());

                ServiceNode source = target;
                String deviceMapId = source.getDevType() + "|" + source.getProdId() + "|" + source.getDeviceId();
                String[] deviceIdArr = source.getServiceId().split("_");
                String deviceMapValue = deviceIdArr[deviceIdArr.length-1];
                deviceMapId = deviceMapId+ "|" + source.getServiceId().replace(String.format("_%s", deviceMapValue),"");
                saveToDeviceMap(deviceMapId, deviceMapValue, deviceServiceList);

                if (allEventActionNodes.size() > 1) {
                    connectLists(appInfo, userGraph, deviceServiceList, allEventActionNodes);
                }
            } else {
                LOGGER.warn("[Warning] Please check the supported device list");
            }

        }
    }

    private void connectLists(AppInfo appInfo, Graph<Object, Object> userGraph, Map<String, Set<String>> deviceServiceList, ArrayList<ServiceNode> allEventActionNodes) {
        for (int num=0; num<allEventActionNodes.size()-1; num++){
            // source node
            ServiceNode source = allEventActionNodes.get(num);
            String deviceMapId = source.getDevType() + "|" + source.getProdId() + "|" + source.getDeviceId();
            String[] deviceIdArr = source.getServiceId().split("_");
            String deviceMapValue = deviceIdArr[deviceIdArr.length-1];
            deviceMapId = deviceMapId+ "|" + source.getServiceId().replace(String.format("_%s", deviceMapValue),"");
            saveToDeviceMap(deviceMapId, deviceMapValue, deviceServiceList);

            String sourceNodeId = source.getDevType() + "|" + source.getProdId() + "|" + source.getDeviceId() + "|" + source.getServiceId();
            // target node
            ServiceNode target = allEventActionNodes.get(num+1);
            String targetNodeId = target.getDevType() + "|" + target.getProdId() + "|" + target.getDeviceId() + "|" +
                    target.getServiceId();
            appInfo.setRelation("control");
            userGraph.setEdge(sourceNodeId, targetNodeId, "control", appInfo.toString());
        }
        if (allEventActionNodes.size() > 1){
            ServiceNode source = allEventActionNodes.get(allEventActionNodes.size()-1);
            String deviceMapId = source.getDevType() + "|" + source.getProdId() + "|" + source.getDeviceId();
            String[] deviceIdArr = source.getServiceId().split("_");
            String deviceMapValue = deviceIdArr[deviceIdArr.length-1];
            deviceMapId = deviceMapId + "|" +source.getServiceId().replace(String.format("_%s", deviceMapValue),"");
            saveToDeviceMap(deviceMapId, deviceMapValue, deviceServiceList);
        }
    }

    private void saveToDeviceMap(String deviceMapId, String deviceMapValue, Map<String, Set<String>> deviceServiceList){
        if (deviceServiceList.containsKey(deviceMapId)){
            deviceServiceList.get(deviceMapId).add(deviceMapValue);
        }else{
            Set<String> deviceValueSet = new HashSet<>();
            deviceValueSet.add(deviceMapValue);
            deviceServiceList.put(deviceMapId, deviceValueSet);
        }
    }

    public void showGraph(Graph<Object, Object> userGraph) {
        LOGGER.info("---- Graph Edge Data Start ----");
        Collection<Edge> edges = userGraph.getEdges();
        for (Edge e: edges){
            LOGGER.info("--");
            LOGGER.info("[Source:] " + e.getSource());
            LOGGER.info("[Relation:] " + e.getName());
            LOGGER.info("[Target:] " + e.getTarget());
            LOGGER.info("--");
        }
        LOGGER.info("---- Graph Edge Data End ----");
    }

    private Graph<Object, Object> addNodeRelation(Graph<Object, Object> userGraph, Map<Integer, String[]> relationCsvMap) {
        Collection<String> nodes = userGraph.getNodes();
        String[] nodesArray = nodes.toArray(new String[0]);

        for (int i = 0; i < nodesArray.length; i++) {
            for (int j = i + 1; j < nodesArray.length; j++) {
                String node1 = nodesArray[i];
                String node2 = nodesArray[j];
                // identify relation
                for (String[] line : relationCsvMap.values()){
                    if (node1.contains(line[1]) && node2.contains(line[2])){
                        userGraph.setEdge(node1, node2, line[3], line[3]);
                    }else if (node1.contains(line[2]) && node2.contains(line[1])){
                        userGraph.setEdge(node2, node1, line[3], line[3]);
                    }
                }
            }
        }
        return userGraph;
    }

    /**
     * Get all files based on target path
     * @param fileInput
     * @param allFileList
     */
    private void getAllFiles(File fileInput, List<File> allFileList){
        File [] fileList = fileInput.listFiles();
        for (File file : fileList){
            if (file.isDirectory()){
                getAllFiles(file, allFileList);
            }else {
                allFileList.add(file);
            }
        }
    }

    public GraphMapPair getAppGraph(String testPath){
        String modelPath = "src/main/resources/knowledgeModel/DeviceModel.csv";
        String relationPath = "src/main/resources/knowledgeModel/relation.csv";

//        String modelPath = "knowledgeModel/DeviceModel.csv";
//        String relationPath = "knowledgeModel/relation.csv";

        // relation map
        Map<Integer, String[]> relationCsvMap = new HashMap<>();
        BufferedReader csvReader = null;
        try {
            csvReader = new BufferedReader(new FileReader(relationPath));
            String row;
            while ((row = csvReader.readLine())!=null){
                String[] data = row.split(",");
                relationCsvMap.put(relationCsvMap.size(), data);
            }
            csvReader.close();

            // load model file
            ConstructGraph cg = new ConstructGraph();
            CSVParser modelCsv = cg.readFromCsvFile(modelPath);
            CSVParser relationCsv = cg.readFromCsvFile(relationPath);
            List<CSVRecord> modelRecords = modelCsv.getRecords();
            List<CSVRecord> relationRecords = relationCsv.getRecords();
            GraphMapPair pair = cg.fileParse(testPath, modelRecords, relationRecords);
            Graph<Object, Object> ECAGraph = pair.getUserGraph();
            Graph<Object, Object> AppGraph = cg.addNodeRelation(ECAGraph, relationCsvMap);
            return pair;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CSVParser readFromCsvFile (String path){
        try {
            Reader reader = new FileReader(path);
            return new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withSkipHeaderRecord(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String testPath = "src/main/resources/testSingle";
        ConstructGraph cg = new ConstructGraph();
        Graph<Object, Object> AppGraph = cg.getAppGraph(testPath).getUserGraph();
        cg.showGraph(AppGraph);
    }

}
