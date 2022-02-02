using System;
using System.Net;
using System.IO;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Networking;
using TMPro;

public class ApiReader : MonoBehaviour
{
    public StreamReader rawData;
    public string procesedData;
    public WWWForm wwwForm;
    public TextMeshProUGUI data;

    HttpWebRequest dbURL = (HttpWebRequest)WebRequest.Create("https://www.condast.com:8443/churuatas/rest/walkers/select?name=keesp&lat=52&lon=6.5&range=10000");
    public List<GameObject> locationList;
    public GameObject empty;
    public GameObject palm;
    public GameObject leaves;

    public void Start()
    {
        
    }

    public void GetData()
    {
        HttpWebResponse response = (HttpWebResponse)dbURL.GetResponse();

        rawData = new StreamReader(response.GetResponseStream());
        data.text = rawData.ReadLine();
    }
}