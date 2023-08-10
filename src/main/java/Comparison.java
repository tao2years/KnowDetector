import com.sun.xml.internal.ws.api.policy.SourceModel;
import graph.Edge;
import graph.Graph;
import model.ResultModel;
import model.ServiceNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

public class Comparison {
    private static final Logger LOGGER = LogManager.getLogger();

    // Manually translate SmartApps
    public Graph<Object, Object> translateApps(){
        Graph<Object, Object> benchmarkGraph = new Graph<>(true,true,false);

        /**
         * ID1.BrightenMyPath
         * <motionSensor, motionDetect, 1> -> <light, switch, on> -> <light, switch, off>
         */
        ServiceNode Id1_1 = new ServiceNode("03E", "2161","TESTSensor", "motionSensor", "motionDetected", "detected", "sensor-TESTSensor_motionSensor_motionDetected_detected", "0");
        ServiceNode Id1_2 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "0");
        ServiceNode Id1_3 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","0","light-TESTLight_switchOnOff_on_0", "0");
        benchmarkGraph.setEdge(Id1_1.toString(), Id1_2.toString(), "control", "control-ID1");
        benchmarkGraph.setEdge(Id1_2.toString(), Id1_3.toString(), "control", "control-ID1");

        /**
         * ID2.SecuritySystem
         * <locationDetector, presentHome, 1> -> <lock, locked, off>
         * <locationDetector, presentHome, 0> -> <securitySystem, switch, off>
         */
        ServiceNode Id2_1 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "home","sensor-TESTSensor_mode_location_home", "0");
        ServiceNode Id2_2 = new ServiceNode("04B","113L","TESTLock" ,"doorLock","switch", "on", "lock-TESTLock_doorLock_switch_on", "0");
        ServiceNode Id2_3 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "noHome","sensor-TESTSensor_mode_location_noHome", "0");
        ServiceNode Id2_4 = new ServiceNode("S1","S100","securitySystem", "switchOnOff", "on","0","system-securitySystem_switchOnOff_on_0","0");
        benchmarkGraph.setEdge(Id2_1.toString(), Id2_2.toString(), "control", "control-ID2");
        benchmarkGraph.setEdge(Id2_3.toString(), Id2_4.toString(), "control", "control-ID2");

        /**
         * ID3.TurnItOnOffandOnEvery30Secs
         * Every 5m <switch, switch, on> -> <switch, switch, off>
         */

        ServiceNode Id3_1 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "E5");
        ServiceNode Id3_2 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","0","light-TESTLight_switchOnOff_on_0", "E5");
        benchmarkGraph.setEdge(Id3_1.toString(), Id3_2.toString(), "control", "control-E5-ID3");

        /**
         * ID4.ID4PowerAllowance
         * Certain time - Power Management <targetDevice, switch, on> -> <targetDevice, switch, off>
         */

        ServiceNode Id4_1 = new ServiceNode("027","100s", "TESTWaterHeater","switchOnOff","on","1","PM-TESTWaterHeater_switchOnOff_on_1" ,"0");
        ServiceNode Id4_2 = new ServiceNode("027","100s", "TESTWaterHeater","switchOnOff","on","0","PM-TESTWaterHeater_switchOnOff_on_0" ,"0");
        benchmarkGraph.setEdge(Id4_1.toString(), Id4_2.toString(), "control", "control-PW-ID4");

        /**
         * ID51.FakeAlarm
         * <COSensor, leakage, 1> -> <alarm, strobe, 1>
         * <COSensor, leakage, 0> -> <alarm, switch, off>
         */
        ServiceNode Id5_1 = new ServiceNode("043","126C","TESTCOSensor", "CODetector", "leakage", "1","TESTCOSensor-CODetector_leakage_1", "0");
        ServiceNode Id5_2 = new ServiceNode("10A","1243", "TESTAlarm", "strobe", "on","1","alarm-TESTAlarm_strobe_on_1", "0");
        ServiceNode Id5_3 = new ServiceNode("043","126C","TESTCOSensor", "CODetector", "leakage", "0","TESTCOSensor-CODetector_leakage_0", "0");
        ServiceNode Id5_4 = new ServiceNode("10A","1243", "TESTAlarm", "switchOnOff", "on","0","alarm-TESTAlarm_switchOnOff_on_0", "0");
        benchmarkGraph.setEdge(Id5_1.toString(), Id5_2.toString(), "control", "control-ID5");
        benchmarkGraph.setEdge(Id5_3.toString(), Id5_4.toString(), "control", "control-ID5");

        /**
         * ID6.TurnOnSwitchNotHome
         * <locationDetector, presentHome, 0> -> <light, switch, on>
         */
        ServiceNode Id6_1 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "noHome","sensor-TESTSensor_mode_location_noHome", "0");
        ServiceNode Id6_2 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "0");
        benchmarkGraph.setEdge(Id6_1.toString(), Id6_2.toString(), "control", "control-ID6");

        /**
         * ID7.ConflictTimeandPresenceSensor
         * <location, presentHome, 1> -> <targetDevice, switch, on>
         * <location, presentHome, 0> -> <targetDevice, switch, off>
         * At specific time. <targetDevice, switch, on> -> <targetDevice, switch, off>
         */
        ServiceNode Id7_1 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "home","sensor-TESTSensor_mode_location_home", "0");
        ServiceNode Id7_2 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "0");
        ServiceNode Id7_3 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "noHome","sensor-TESTSensor_mode_location_noHome", "0");
        ServiceNode Id7_4 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","0","light-TESTLight_switchOnOff_on_0", "0");
        benchmarkGraph.setEdge(Id7_1.toString(), Id7_2.toString(), "control", "control-ID7");
        benchmarkGraph.setEdge(Id7_3.toString(), Id7_4.toString(), "control", "control-ID7");
        benchmarkGraph.setEdge(Id7_2.toString(), Id7_4.toString(), "control", "control-ET-ID7");

        /**
         * ID8.LocationSubscribeFailure
         * skip
         */

        /**
         * ID9.DisableVacationMode
         * skip
         */

        /**
         * N1.Turn on the flashlight after turning on the camera on your phone
         * <camera, switch, on> -> <flashlight, switch, on>
         */
        ServiceNode ID9_1 = new ServiceNode("mobile","X02","mobileX02","camera","switch","on", "phone-iPhoneX_flashlight_camera_on","0");
        ServiceNode ID9_2 = new ServiceNode("mobile","X02","mobileX02","flashlight","switch","on", "phone-iPhoneX_flashlight_switch_on","0");
        benchmarkGraph.setEdge(ID9_1.toString(), ID9_2.toString(), "control", "control");
        benchmarkGraph.setEdge(ID9_1.toString(), ID9_2.toString(), "control", "exclusive");
        benchmarkGraph.setEdge(ID9_2.toString(), ID9_1.toString(),"control", "exclusive");

        /**
         * Bundle 1
         */
        ServiceNode IdB1_1 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "home","sensor-TESTSensor_mode_location_home", "0");
        ServiceNode IdB1_2 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "0");
        ServiceNode IdB1_3 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "home","sensor-TESTSensor_mode_location_home", "0");
        ServiceNode IdB1_4 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "0");
        ServiceNode IdB1_5 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "noHome","sensor-TESTSensor_mode_location_noHome", "0");
        ServiceNode IdB1_6 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","0","light-TESTLight_switchOnOff_on_0", "0");
        benchmarkGraph.setEdge(IdB1_1.toString(), IdB1_2.toString(),"control","control-B1");
        benchmarkGraph.setEdge(IdB1_3.toString(), IdB1_4.toString(),"control","control-B1");
        benchmarkGraph.setEdge(IdB1_5.toString(), IdB1_6.toString(),"control","control-B1");

        /**
         * Bundle 2
         */
        ServiceNode IdB2_1 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "home","sensor-TESTSensor_mode_location_home", "0");
        ServiceNode IdB2_2 = new ServiceNode("04B","113L","TESTLock" ,"doorLock","switch", "on", "lock-TESTLock_doorLock_switch_on", "0");
        ServiceNode IdB2_3 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "sleeping", "detected","sensor-TESTSensor_mode_sleeping_detected", "0");
        ServiceNode IdB2_4 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","0","light-TESTLight_switchOnOff_on_0", "0");
        benchmarkGraph.setEdge(IdB2_1.toString(), IdB2_2.toString(),"control","control-B2");
        benchmarkGraph.setEdge(IdB2_3.toString(), IdB2_4.toString(),"control","control-PW-B2");

        /**
         * Bundle 3
         */
        ServiceNode IdB3_1 = new ServiceNode("10A","1243", "TESTAlarm", "switchOnOff", "on","1","alarm-TESTAlarm_switchOnOff_on_1", "0");
        ServiceNode IdB3_2 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "0");
        ServiceNode IdB3_3 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "0");
        ServiceNode IdB3_4 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "home","sensor-TESTSensor_mode_location_home", "0");
        ServiceNode IdB3_5 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "home","sensor-TESTSensor_mode_location_home", "0");
        ServiceNode IdB3_6 = new ServiceNode("04B","113L","TESTLock" ,"doorLock","switch", "on", "lock-TESTLock_doorLock_switch_on", "0");
        benchmarkGraph.setEdge(IdB3_1.toString(), IdB3_2.toString(),"control","control-B3");
        benchmarkGraph.setEdge(IdB3_3.toString(), IdB3_4.toString(),"control","control-B3");
        benchmarkGraph.setEdge(IdB3_5.toString(), IdB3_6.toString(),"control","control-B3");
        benchmarkGraph.setEdge(IdB3_1.toString(), IdB3_6.toString(),"control","control-B3");

        /**
         * Bundle 4
         */
        ServiceNode IdB4_1 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "location", "noHome","sensor-TESTSensor_mode_location_noHome", "0");
        ServiceNode IdB4_2 = new ServiceNode("04B","113L","TESTLock" ,"doorLock","switch", "off", "lock-TESTLock_doorLock_switch_off", "0");
        ServiceNode IdB4_3 = new ServiceNode("043","126C","TESTCOSensor", "CODetector", "leakage", "1","TESTCOSensor-CODetector_leakage_1", "0");
        ServiceNode IdB4_4 = new ServiceNode("04B","113L","TESTLock" ,"doorLock","switch", "on", "lock-TESTLock_doorLock_switch_on", "0");
        benchmarkGraph.setEdge(IdB4_1.toString(), IdB4_2.toString(),"control","control-B4");
        benchmarkGraph.setEdge(IdB4_3.toString(), IdB4_4.toString(),"control","control-B4");

        /**
         * Bundle 5
         */
        ServiceNode IdB5_1 = new ServiceNode("01L", "L100","TESTSensor" ,"mode", "motion", "detected","sensor-TESTSensor_mode_motion_detected", "0");
        ServiceNode IdB5_2 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "0");
        ServiceNode IdB5_3 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","0","light-TESTLight_switchOnOff_on_0", "0");
        benchmarkGraph.setEdge(IdB5_1.toString(), IdB5_2.toString(),"control","control-B5");
        benchmarkGraph.setEdge(IdB5_2.toString(), IdB5_3.toString(),"control","control-B5");

        /**
         * Bundle 6
         */
        ServiceNode IdB6_1 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "0");
        ServiceNode IdB6_2 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","0","light-TESTLight_switchOnOff_on_0", "0");
        ServiceNode IdB6_3 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","1","light-TESTLight_switchOnOff_on_1", "5");
        ServiceNode IdB6_4 = new ServiceNode("01E","100B","TESTLight", "switchOnOff","on","0","light-TESTLight_switchOnOff_on_0", "5");
        benchmarkGraph.setEdge(IdB6_1.toString(), IdB6_2.toString(),"control","control-B6");
        benchmarkGraph.setEdge(IdB6_3.toString(), IdB6_4.toString(),"control","control-E5-B6");

        return benchmarkGraph;
    }

    public void showGraph (Graph<Object, Object> graph) {
        Collection<Edge> edgeCollection = graph.getEdges();
        for (Edge e : edgeCollection) {
            LOGGER.info(String.format("Source: %s, Target: %s, Name: %s.", e.getSource(), e.getTarget(), e.getName()));
        }
    }

    public void propertyDetection(Graph<Object, Object> graph){
        Collection<Edge> edgeCollection = graph.getEdges();
        for (Edge e : edgeCollection) {
            // ID1 - P2. The lights (in a bedroom, hallway, etc.) must be turned on if the motion sensor is active.
            //       S1. An event handler must not change a device attribute to conflicting values
            String source = e.getSource();
            String target = e.getTarget();
            String name = e.getName();

            ServiceNode sourceNode = ServiceNode.fromString(source);
            ServiceNode targetNode = ServiceNode.fromString(target);

            boolean S1 = P_S1(sourceNode, targetNode, name);
            if (S1) {
                LOGGER.info("Violate S1. " + e);
            }

            // ID2 - P9. The security system must not be disarmed when the user is not at home.
            boolean S9 = P_S9(sourceNode, targetNode, name);
            if (S9) {
                LOGGER.info("Violate S9. " + e);
            }
            // ID3 - S1. An event handler must not change a device attribute to conflicting values
            // No match

            // ID4 - S1. An event handler must not change a device attribute to conflicting values
            // Skip Power Management

            // ID51 - No problem

            // ID6 - P12. The devices (e.g., light switches, cleaning supply cabinets, medicine drawers, or gun cases) must not be open or turned on when the
            // user is not at home or sleeping.
            boolean S12 = P_S12(sourceNode, targetNode, name);
            if (S12) {
                LOGGER.info("Violate S12. " + e);
            }

            boolean S14 = P_14(sourceNode, targetNode, name);
            if (S14) {
                LOGGER.info("Violate P14. "+ e);
            }

            boolean N1 = P_N1(sourceNode, targetNode, name);
            if (N1){
                LOGGER.info("Violate N1. "+ e);
            }

            boolean P1 = P_1(sourceNode, targetNode, name);
            if (P1){
                LOGGER.info("Violate P1. "+ e);
            }


        }
        // ID7 - S4.
        // Two or more non-complement event handlers must
        // not change a device attribute to conflicting values, e.g., a user-present event handler turns on the
        // switch while a timer event handler turns off the
        // switch at midnight. This is because the events of
        // user presence and midnight may occur at the same
        // time, leading to a race condition.
        P_S4(graph);

        // N1 - Turn on flashlight and camera
        // Blocked Action
        Detection dt = new Detection();
        ArrayList<ResultModel> results = new ArrayList<>();
        dt.blockedActionDetection(graph, results);
        if (results.size() > 0){
            LOGGER.info("Violate Blocked Action");
            LOGGER.info(results);
        }
    }

    /**
     * S1. An event handler must not change a device attribute to conflicting values
     * @param source
     * @param target
     * @param name
     * @return
     */
    public boolean P_S1 (ServiceNode source, ServiceNode target, String name){
        if (name.contains("control"))
            return identifySameCapabilityConflictValue(source, target);
        return false;
    }

    /**
     * S9. The security system must not be disarmed when the user is not at home.
     * @param source
     * @param target
     * @return
     */
    public boolean P_S9 (ServiceNode source, ServiceNode target, String name){
        return (source.getServiceId().contains("Sensor_mode_location_noHome") &&
                target.getServiceId().contains("securitySystem_switchOnOff_on_0"));
    }

    /**
     * S12. The devices (e.g., light switches, cleaning supply cabinets, medicine drawers, or gun cases)
     * must not be open or turned on when the user is not at home or sleeping.
     * @param source
     * @param target
     * @param name
     * @return
     */
    public boolean P_S12 (ServiceNode source, ServiceNode target, String name){
        // For simplicity, we only judge light first.
        return (source.getServiceId().contains("Sensor_mode_location_noHome") &&
                target.getServiceId().contains("Light_switchOnOff_on_1"));
    }

    /**
     * The refrigerator, alarm, and security system must not be disabled,
     *  and their use must not be restricted to save energy.
     * @param source
     * @param target
     * @param name
     * @return
     */
    public boolean P_14(ServiceNode source, ServiceNode target, String name){
        if (name.contains("control-PW"))
            return true;
        return false;
    }

    /**
     * When alarming, the door should not be locked
     * @param source
     * @param target
     * @param name
     * @return
     */
    public boolean P_N1(ServiceNode source, ServiceNode target, String name){
        return source.getServiceId().contains("Alarm_switchOnOff_on_1") && target.getServiceId().contains("Lock_doorLock_switch_on");
    }

    /**
     * Two or more non-complement event handlers must not change a device attribute to conflicting values,
     * e.g., a user-present event handler turns on the switch while a timer event handler turns off the
     * switch at midnight. This is because the events of user presence and midnight may occur at the same
     * time, leading to a race condition.
     * @param graph
     * @return
     */
    public boolean P_S4 (Graph<Object, Object> graph) {
        Collection<Edge> edgeCollection = graph.getEdges();
        boolean flag = false;
        for (Edge e1 : edgeCollection) {
            for (Edge e2 : edgeCollection){
                for (Edge e3 : edgeCollection) {
                    boolean s1 = e1.getSource().contains("Sensor_mode_location_home")  && e1.getTarget().contains("Light_switchOnOff_on_1");
                    boolean s2 = e2.getSource().contains("Sensor_mode_location_noHome") &&  e2.getTarget().contains("Light_switchOnOff_on_0");
                    boolean s3 = e3.getTarget().contains("Light_switchOnOff_on_0")  && e3.getName().contains("control-E");
                    if (s1 && s2 && s3) {
                        LOGGER.info("Violate S4. The edges involved are:");
                        LOGGER.info("[1] " + e1);
                        LOGGER.info("[2] " + e2);
                        LOGGER.info("[3] " + e3);
                        flag =true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * The door must be locked when a user is not present at home or sleeping.
     * @param source
     * @param target
     * @param name
     * @return
     */
    public boolean P_1(ServiceNode source, ServiceNode target, String name){
        return (source.getServiceId().contains("Sensor_mode_location_noHome") ||
                source.getServiceId().contains("Sensor_mode_sleeping_detected")) &&
                target.getServiceId().contains("Lock_doorLock_switch_off");
    }

    /**
     * Identify same capability with conflicting values/commands
     * @param source
     * @param target
     * @return
     */
    public boolean identifySameCapabilityConflictValue(ServiceNode source, ServiceNode target){
        if (source.getDeviceId().equals(target.getDeviceId())){
            return source.getServiceWithOutValue().equals(target.getServiceWithOutValue()) && !source.getValue().equals(target.getValue());
        }
        return false;
    }

    public static void main(String[] args) {
        Comparison cp = new Comparison();
        Graph<Object, Object> graph = cp.translateApps();
        cp.showGraph(graph);
        cp.propertyDetection(graph);
    }
}
