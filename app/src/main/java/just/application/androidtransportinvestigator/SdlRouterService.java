package just.application.androidtransportinvestigator;

/**
 * The SdlRouterService listens for a bluetooth connection with an SDL enabled module.
 * When a connection happens, it will alert all SDL enabled apps
 * that a connection has been established and they should start their SDL services.
 */
public class SdlRouterService extends  com.smartdevicelink.transport.SdlRouterService {
}