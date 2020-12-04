package openMaps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class JsonBruteForce {
    private Object finalresult = null;

    public Object findFirstValuetoKey(JSONObject x, String y) throws JSONException {
        JSONArray keys = x.names();
        for (int i = 0; i < keys.length(); i++) {
            if (finalresult != null) {
                return finalresult;
            }

            String current_key = keys.get(i)
                                     .toString();

            if (current_key.equals(y)) {
                finalresult = x.get(current_key);
                return finalresult;
            }

            if (x.get(current_key)
                 .getClass()
                 .getName()
                 .equals("org.json.JSONObject")) {
                findFirstValuetoKey((JSONObject) x.get(current_key), y);
            } else if (x.get(current_key)
                        .getClass()
                        .getName()
                        .equals("org.json.JSONArray")) {
                for (int j = 0; j < ((JSONArray) x.get(current_key)).length(); j++) {
                    if (((JSONArray) x.get(current_key)).get(j)
                                                        .getClass()
                                                        .getName()
                                                        .equals("org.json.JSONObject")) {
                        findFirstValuetoKey((JSONObject) ((JSONArray) x.get(current_key)).get(j), y);
                    }
                }
            }
        }
        return null;
    }
}