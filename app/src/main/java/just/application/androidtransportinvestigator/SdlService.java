package just.application.androidtransportinvestigator;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ButtonPressResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetSystemCapabilityResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetWayPointsResponse;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnInteriorVehicleData;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.OnWayPointChange;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SendHapticDataResponse;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.transport.BTTransportConfig;
import com.smartdevicelink.transport.BaseTransportConfig;
import com.smartdevicelink.transport.MultiplexTransportConfig;
import com.smartdevicelink.transport.TCPTransportConfig;
import com.smartdevicelink.transport.TransportConstants;
import com.smartdevicelink.transport.USBTransportConfig;

/**
 * A SdlService manages the lifecycle of an SDL Proxy.
 * The SDLService enables auto-start by creating the SDL Proxy, which then waits for a connection from SDL.
 * This file also sends and receives messages to and from SDL after connected.
 **/
public class SdlService extends Service implements IProxyListenerALM {

    private static final String TAG = "SdlService";

    private static final String APP_NAME = "SDL-Android Transport Investigator";
    private static final String APP_ID = "8675309";
    private static final int FOREGROUND_SERVICE_ID = 111;

    // TCP/IP transport config
    // The default port is 12345
    // The IP is of the machine that is running SDL Core
    private static final int TCP_PORT = 12345;
    private static final String DEV_MACHINE_IP_ADDRESS = "172.31.239.143"; //TODO read ip from input

    // variable to create and call functions of the SyncProxy
    private SdlProxyALM proxy = null;

    private boolean firstNonHmiNone = true;
    @SuppressWarnings("unused")
    private boolean isVehicleDataSubscribed = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        //Because of Android Oreo's requirements, it is mandatory that services enter the foreground for long running tasks.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterForeground();
        }
    }

    @SuppressLint("NewApi")
    public void enterForeground() {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Connected through SDL")
                .setSmallIcon(R.drawable.ic_sdl)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .build();
        startForeground(FOREGROUND_SERVICE_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean forced = intent != null && intent.getBooleanExtra(TransportConstants.FORCE_TRANSPORT_CONNECTED, false);
        String transportType = intent.getStringExtra((String) "TransportType");
        String bluetoothType = intent.getStringExtra((String) "BluetoothType");
        String bluetoothSecurityLevel = intent.getStringExtra((String) "BluetoothSecurityLevel");

        startProxy(forced, intent, transportType, bluetoothType, bluetoothSecurityLevel);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        disposeSyncProxy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        super.onDestroy();
    }

    private void startProxy(boolean forceConnect,
                            Intent intent,
                            String transportType,
                            String bluetoothType,
                            String bluetoothSecurityLevel) {

        Log.i(TAG, "Trying to start proxy. TransportType is " + transportType);

        if (null == proxy) {
            try {
                Log.i(TAG, "Starting SDL Proxy");
                BaseTransportConfig transport = null;

                switch (transportType) {
                    case "USB":
                        if (intent != null && intent.hasExtra(UsbManager.EXTRA_ACCESSORY)) { //If we want to support USB transport
                            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
                                Log.e(TAG, "Unable to start proxy. Android OS version is too low");
                                return;
                            } else {
                                //We have a usb transport
                                transport = new USBTransportConfig(getBaseContext(), (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY));
                                Log.d(TAG, "USB created.");
                            }
                        } else {
                            Log.e(TAG, "SHIT");
                        }
                        break;

                    case "TCP":
                        transport = new TCPTransportConfig(TCP_PORT, DEV_MACHINE_IP_ADDRESS, true);
                        break;

                    case "BT":
                        if (bluetoothType.equals("MBT")) {

                            int securityLevel;

                            if(bluetoothSecurityLevel.equals("HIGH")){
                                securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_HIGH;
                            } else if (bluetoothSecurityLevel.equals("MED")) {
                                securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_MED;
                            } else if (bluetoothSecurityLevel.equals("LOW")) {
                                securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_LOW;
                            } else {
                                securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF;
                            }

                            transport = new MultiplexTransportConfig(this, APP_ID, securityLevel);

                        } else if (bluetoothSecurityLevel.equals("LBT")) {
                            transport = new BTTransportConfig();
                        } else {
                            Log.e(TAG, "Unable to start proxy. Need to specify Bluetooth protocol");
                            return;
                        }
                        break;

                    default:
                        break;
                }

                if (transport != null) {
                    proxy = new SdlProxyALM(this, APP_NAME, true, APP_ID, transport);
                } else {
                    Log.w(TAG, "Proxy was not created. Input params: transportType = " + transportType +
                    "; bluetoothType = " + bluetoothType +
                    "; bluetoothSecurityLevel = " + bluetoothSecurityLevel);
                }

            } catch (SdlException e) {
                e.printStackTrace();
                // error creating proxy, returned proxy = null
                if (null == proxy) {
                    stopSelf();
                }
            }
        } else if(forceConnect) {
            proxy.forceOnConnected();
        }
    }

    private void disposeSyncProxy() {

        if (proxy != null) {
            try {
                proxy.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                proxy = null;
            }
        }

        this.firstNonHmiNone = true;
        this.isVehicleDataSubscribed = false;
    }

    @Override
    public void onProxyClosed(String info, Exception e, SdlDisconnectedReason reason) {
    }

    @Override
    public void onOnHMIStatus(OnHMIStatus notification) {
        if(notification.getHmiLevel().equals(HMILevel.HMI_FULL)){
            if (notification.getFirstRun()) {
                Log.i(TAG, "HMI_FULL");
            }
            // Other HMI (Show, PerformInteraction, etc.) would go here
        }

        if(!notification.getHmiLevel().equals(HMILevel.HMI_NONE)
                && firstNonHmiNone){
            Log.i(TAG, "HMI_NONE");
            firstNonHmiNone = false;

            // Other app setup (SubMenu, CreateChoiceSet, etc.) would go here
        }else{
            //We have HMI_NONE
            if(notification.getFirstRun()){
                Log.i(TAG, "HMI level other");
            }
        }
    }

    @Override
    public void onListFilesResponse(ListFilesResponse response) {
        Log.i(TAG, "onListFilesResponse from SDL ");
    }

    @Override
    public void onPutFileResponse(PutFileResponse response) {
        Log.i(TAG, "onPutFileResponse from SDL");
    }

    @Override
    public void onOnLockScreenNotification(OnLockScreenStatus notification) {
        //TODO
    }

    @Override
    public void onOnCommand(OnCommand notification){
        //TODO
    }

    /**
     *  Callback method that runs when the add command response is received from SDL.
     */
    @Override
    public void onAddCommandResponse(AddCommandResponse response) {
        Log.i(TAG, "AddCommand response from SDL: " + response.getResultCode().name());

    }

    /*  Vehicle Data   */
    @Override
    public void onOnPermissionsChange(OnPermissionsChange notification) {
        Log.i(TAG, "Permision changed: " + notification);
    }

    /**
     * Rest of the SDL callbacks from the head unit
     */

    @Override
    public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {
        Log.i(TAG, "Subscribe Vehicle Data Response notification from SDL");
    }

    @Override
    public void onOnVehicleData(OnVehicleData notification) {
        Log.i(TAG, "Vehicle data notification from SDL");
        //TODO Put your vehicle data code here
        //ie, notification.getSpeed().
    }

    @Override
    public void onAddSubMenuResponse(AddSubMenuResponse response) {
        Log.i(TAG, "AddSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {
        Log.i(TAG, "CreateInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onAlertResponse(AlertResponse response) {
        Log.i(TAG, "Alert response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onDeleteCommandResponse(DeleteCommandResponse response) {
        Log.i(TAG, "DeleteCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {
        Log.i(TAG, "DeleteInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
        Log.i(TAG, "DeleteSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onPerformInteractionResponse(PerformInteractionResponse response) {
        Log.i(TAG, "PerformInteraction response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onResetGlobalPropertiesResponse(
            ResetGlobalPropertiesResponse response) {
        Log.i(TAG, "ResetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
        Log.i(TAG, "SetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
        Log.i(TAG, "SetMediaClockTimer response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onShowResponse(ShowResponse response) {
        Log.i(TAG, "Show response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSpeakResponse(SpeakResponse response) {
        Log.i(TAG, "SpeakCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnButtonEvent(OnButtonEvent notification) {
        Log.i(TAG, "OnButtonEvent notification from SDL: " + notification);
    }

    @Override
    public void onOnButtonPress(OnButtonPress notification) {
        Log.i(TAG, "OnButtonPress notification from SDL: " + notification);
    }

    @Override
    public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
        Log.i(TAG, "SubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
        Log.i(TAG, "UnsubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnTBTClientState(OnTBTClientState notification) {
        Log.i(TAG, "OnTBTClientState notification from SDL: " + notification);
    }

    @Override
    public void onUnsubscribeVehicleDataResponse(
            UnsubscribeVehicleDataResponse response) {
        Log.i(TAG, "UnsubscribeVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onGetVehicleDataResponse(GetVehicleDataResponse response) {
        Log.i(TAG, "GetVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onReadDIDResponse(ReadDIDResponse response) {
        Log.i(TAG, "ReadDID response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onGetDTCsResponse(GetDTCsResponse response) {
        Log.i(TAG, "GetDTCs response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {
        Log.i(TAG, "PerformAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {
        Log.i(TAG, "EndAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnAudioPassThru(OnAudioPassThru notification) {
        Log.i(TAG, "OnAudioPassThru notification from SDL: " + notification );
    }

    @Override
    public void onDeleteFileResponse(DeleteFileResponse response) {
        Log.i(TAG, "DeleteFile response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSetAppIconResponse(SetAppIconResponse response) {
        Log.i(TAG, "SetAppIcon response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onScrollableMessageResponse(ScrollableMessageResponse response) {
        Log.i(TAG, "ScrollableMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {
        Log.i(TAG, "ChangeRegistration response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {
        Log.i(TAG, "SetDisplayLayout response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnLanguageChange(OnLanguageChange notification) {
        Log.i(TAG, "OnLanguageChange notification from SDL: " + notification);
    }

    @Override
    public void onSliderResponse(SliderResponse response) {
        Log.i(TAG, "Slider response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnHashChange(OnHashChange notification) {
        Log.i(TAG, "OnHashChange notification from SDL: " + notification);
    }

    @Override
    public void onOnSystemRequest(OnSystemRequest notification) {
        Log.i(TAG, "OnSystemRequest notification from SDL: " + notification);
    }

    @Override
    public void onSystemRequestResponse(SystemRequestResponse response) {
        Log.i(TAG, "SystemRequest response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnKeyboardInput(OnKeyboardInput notification) {
        Log.i(TAG, "OnKeyboardInput notification from SDL: " + notification);
    }

    @Override
    public void onOnTouchEvent(OnTouchEvent notification) {
        Log.i(TAG, "OnTouchEvent notification from SDL: " + notification);
    }

    @Override
    public void onDiagnosticMessageResponse(DiagnosticMessageResponse response) {
        Log.i(TAG, "DiagnosticMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnStreamRPC(OnStreamRPC notification) {
        Log.i(TAG, "OnStreamRPC notification from SDL: " + notification);
    }

    @Override
    public void onStreamRPCResponse(StreamRPCResponse response) {
        Log.i(TAG, "StreamRPC response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onDialNumberResponse(DialNumberResponse response) {
        Log.i(TAG, "DialNumber response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSendLocationResponse(SendLocationResponse response) {
        Log.i(TAG, "SendLocation response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onServiceEnded(OnServiceEnded serviceEnded) {

    }

    @Override
    public void onServiceNACKed(OnServiceNACKed serviceNACKed) {

    }

    @Override
    public void onShowConstantTbtResponse(ShowConstantTbtResponse response) {
        Log.i(TAG, "ShowConstantTbt response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onAlertManeuverResponse(AlertManeuverResponse response) {
        Log.i(TAG, "AlertManeuver response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onUpdateTurnListResponse(UpdateTurnListResponse response) {
        Log.i(TAG, "UpdateTurnList response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onServiceDataACK(int dataSize) {

    }

    @Override
    public void onGetWayPointsResponse(GetWayPointsResponse response) {
        Log.i(TAG, "GetWayPoints response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSubscribeWayPointsResponse(SubscribeWayPointsResponse response) {
        Log.i(TAG, "SubscribeWayPoints response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onUnsubscribeWayPointsResponse(UnsubscribeWayPointsResponse response) {
        Log.i(TAG, "UnsubscribeWayPoints response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnWayPointChange(OnWayPointChange notification) {
        Log.i(TAG, "OnWayPointChange notification from SDL: " + notification);
    }

    @Override
    public void onOnDriverDistraction(OnDriverDistraction notification) {
         //Some RPCs (depending on region) cannot be sent when driver distraction is active.
    }

    @Override
    public void onError(String info, Exception e) {
    }

    @Override
    public void onGenericResponse(GenericResponse response) {
        Log.i(TAG, "Generic response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onGetSystemCapabilityResponse(GetSystemCapabilityResponse response) {
        Log.i(TAG, "GetSystemCapabilityResponse from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSendHapticDataResponse(SendHapticDataResponse response){
        Log.i(TAG, "SendHapticData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onButtonPressResponse(ButtonPressResponse response) {
        Log.i(TAG, "ButtonPress response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSetInteriorVehicleDataResponse(SetInteriorVehicleDataResponse response) {
        Log.i(TAG, "SetInteriorVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onGetInteriorVehicleDataResponse(GetInteriorVehicleDataResponse response) {
        Log.i(TAG, "GetInteriorVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnInteriorVehicleData(OnInteriorVehicleData notification) {
        Log.i(TAG, "OnInteriorVehicleData from SDL: " + notification);
    }
}
