package com.beingdev.magicprint.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iwish on bag/1/2016.
 */
public class JsonHelper {
    protected String json_str = null;
    JSONObject jsonObj = null;
    JSONArray jsonArray = null;
    public JsonHelper(String json)
    {
        this.json_str = json;

    }

      public boolean isValidJson()
        {
            try{
              this.jsonObj =   new JSONObject(this.json_str);

            }catch (JSONException jse)
            {
                try {
                    new JSONArray(this.json_str);

                }catch (JSONException jsm){
                    return  false;
                }

            }

            return true;

        }

    public String GetResult(String json_key)
    {

        String results = null;
        // Getting JSON Array node
        try {

             results =  this.jsonObj.getString(json_key).toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }


    public JSONObject setChildjsonObj(JSONObject jsonObj, String jsonobj){

        try {
            this.jsonObj = jsonObj.getJSONObject(jsonobj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.jsonObj;
    }

    public void setChildjsonObj(JSONArray jsonObj, int jsonobj){

        try {
            this.jsonObj = jsonObj.getJSONObject(jsonobj);
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public JSONArray setChildjsonArray(JSONObject jsonObj, String jsonobjstring){

        try {
            this.jsonArray = jsonObj.getJSONArray(jsonobjstring);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  this.jsonArray;
    }

    public JSONObject getCurrentJsonObj(){
        return this.jsonObj;
    }
    public JSONArray getCurrentJsonArray(){
        return this.jsonArray;
    }

        public int getCurrentJsonArrayLength()
        {
            return this.jsonArray.length();
        }






}
