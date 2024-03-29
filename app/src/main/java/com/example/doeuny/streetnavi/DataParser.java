package com.example.doeuny.streetnavi;

import org.json.JSONObject;

import java.util.HashMap;
        import android.util.Log;

        import com.google.android.gms.maps.model.LatLng;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;

/**
 * Created by MIN on 2017-08-14.
 */

public class DataParser {// Google Direction APi 받아온거 파싱 (길찾기) //https://developers.google.com/maps/documentation/geocoding/start?hl=ko

    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
                List l_DD = new ArrayList<>();
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    HashMap<String,String> h_DD = new HashMap<>(); // 거리, 소요시간 저장할 해쉬맵(키,벨류 사용한다)
                    JSONObject jDis = ((JSONObject)jLegs.get(i)).getJSONObject("distance"); // distance 키값 오브젝트 가져온다.
                    JSONObject jDur = ((JSONObject)jLegs.get(i)).getJSONObject("duration"); // duration 키값 오브젝트 가져온다.
                    h_DD.put("Distance",jDis.get("text").toString()); // Distance에서 text부분(거리가 얼마인지나와있는 부분) 가져와서 해쉬맵에 넣는다.
                    h_DD.put("Duration",jDur.get("text").toString()); // Duration에서 text부분(소요시간이 얼마인지나와있는 부분) 가져와서 해쉬맵에 넣는다.
                    Log.d("d_parsing","DP:"+ h_DD.get("Distance"));
                    path.add(h_DD);

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {

                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                    //  routes.add(l_DD);
                }
            }
        }//try
        catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return routes;
    }

    public List<String> R_geocoding(JSONObject jObject) // 구글 Google Maps Geocoding API 이용 파싱  // https://developers.google.com/maps/documentation/geocoding/start?hl=ko
    {
        List<String> f_a= new ArrayList<>();
        JSONArray jResults;
        JSONArray jAddress;
        try {
            jResults = jObject.getJSONArray("results");
            jAddress = ((JSONObject)jResults.get(0)).getJSONArray("address_components"); // 주소정보 가져와서 JSon 객체에 넣음
            f_a.add(((JSONObject)(jAddress.get(0))).get("long_name").toString()+" " +((JSONObject)(jAddress.get(1))).get("long_name").toString()); // 그 객체에서 long_name부분 가져와서 리턴할 리스트에 넣는다.
            f_a.add(((JSONObject)jResults.get(0)).get("formatted_address").toString()); // 비슷
            Log.d("R_g", f_a.get(0) );

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("R_g","R_g error");
        }
        return f_a;
    }

    public List<List<String>> parse_DT(JSONObject jObject) // 안쓴다. 거리, 소요시간을 원래 여기서 파싱했는데, Google Distance Matrix Api 이용해서, 근데 Google Direction (맨위) 에서 뽑아 쓸수 있어서 위에서 뽑아 쓰는거로 변경,  공부는 많이 됬다.

    {
        JSONArray jRows;
        JSONArray jElements;

        List<List<String>> data = new ArrayList<>();
        List <String> iDistance = new ArrayList<>();
        List <String> iDuration = new ArrayList<>();
        //elements 는 여러개고
        // distance,duration 조합이 여러개.
        // e
        // distance.get("text") for문 엘리먼츠 사이즈만큼.
        Log.d("DisDur","1");
        try {
            Log.d("DisDur","2");
            jRows = jObject.getJSONArray("rows");
            for(int i=0;i<jRows.length();i++)
            {

                jElements = ((JSONObject)jRows.get(i)).getJSONArray("elements");
                Log.d("DisDur",jElements.get(0).toString());
                for(int j=0;j<jElements.length();j++)
                {
                    Log.d("DisDur","4");
                    JSONObject jDis = ((JSONObject)jElements.get(i)).getJSONObject("distance");
                    JSONObject jDur = ((JSONObject)jElements.get(i)).getJSONObject("duration");

                    Log.d("DisDur",jDis.get("text").toString());
                    Log.d("DisDur",jDur.get("text").toString());

                    iDistance.add(jDis.get("text").toString());
                    iDuration.add(jDur.get("text").toString());
                }
            }
            Log.d("DisDur","5");
            data.add(iDistance);
            data.add(iDuration);


        } catch (JSONException e) {
            Log.d("DisDur","catch당함");
            e.printStackTrace();
        }
        for(int i=0;i<data.get(0).size();i++)
        {
            Log.d("DisDur",data.get(0).get(i)+"\n"+data.get(1).get(i)); //get0 : 거리 , get(1) : 시간
        }
        return data;
        //       System.out.println("??");
    }


    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */

    private List<LatLng> decodePoly(String encoded) { //  루트 그려주는 폴리라인 하는건데 걍 가져다 씀
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


}