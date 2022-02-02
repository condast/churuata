using UnityEngine;
using System.Net;
using System.IO;

public static class APIHelper
{
    public static RestCallTest rest()
    {
        HttpWebRequest request = (HttpWebRequest)WebRequest.Create("https://www.condast.com:8443/churuatas/rest/walkers/select?name=keesp&lat=52&lon=6.5&range=10000");
        HttpWebResponse response = (HttpWebResponse)request.GetResponse();
        StreamReader reader = new StreamReader(response.GetResponseStream());
        string json = reader.ReadToEnd();
        return JsonUtility.FromJson<RestCallTest>(json);
    }
}