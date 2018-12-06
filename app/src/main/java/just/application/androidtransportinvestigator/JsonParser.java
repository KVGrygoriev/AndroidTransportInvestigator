package just.application.androidtransportinvestigator;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class JsonParser {

    static final String TAG = "JsonParser";

    static JsonObject parseJsonString(final String incomingJsonString) {

        JsonObject jsonObject = null;

        try {
            jsonObject = new com.google.gson.JsonParser().parse(incomingJsonString).getAsJsonObject();
        } catch (JsonParseException e) {
            Log.e(TAG, "Can't parse invalid JSON: " + incomingJsonString, e);
        } finally {
            return jsonObject;
        }
    }

    static String getAsString(final JsonObject object, final String attribute) {

        final JsonElement el = object.get(attribute);

        if (null == el) {
            return null;
        }

        String attributeValue = null;

        try {
            attributeValue = el.getAsString();
        } catch (ClassCastException e) {
            Log.e(TAG, "Can't get value as String. Exception: " , e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Can't get value as String. Exception: " , e);
        }
        finally {
            return attributeValue;
        }
    }

    static Integer getAsInt(final JsonObject object, final String attribute) {

        final JsonElement el = object.get(attribute);

        if (null == el) {
            return null;
        }

        Integer attributeValue = null;

        try {
            attributeValue = el.getAsInt();
        } catch (ClassCastException e) {
            Log.e(TAG, "Can't get value as Integer. Exception: " , e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Can't get value as Integer. Exception: " , e);
        }
        finally {
            return attributeValue;
        }
    }
}
