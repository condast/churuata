using System;
using System.Net;
using System.IO;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Networking;



public class Builder : MonoBehaviour
{

    
    public string testData;
    public StreamReader rawData;
    public string[] procesedData;
    public string[] saveLock;
    //
    HttpWebRequest dbURL = (HttpWebRequest)WebRequest.Create("https://www.condast.com:8443/churuatas/rest/walkers/select?name=keesp&lat=52&lon=6.5&range=10000");
    public List<GameObject> locationList;
    public List<Canvas> churuataLocation;
    public GameObject type1;
    public GameObject type2;
    public GameObject type3;
    public GameObject palm;
    public GameObject leaves;
    
    void Start()
    {
        HandleData(testData);
        /*
        HttpWebResponse response = (HttpWebResponse)dbURL.GetResponse();

        rawData = new StreamReader(response.GetResponseStream());
        Debug.Log(rawData.ReadToEnd());
        */
       ConstructList();
    }
    public void HandleData(string data)
    {
        procesedData = data.Split('/');

        saveLock = data.Split('|');
    }
    private void ConstructList()
    {

        int i = 0;
        foreach (string data in saveLock)
        {
            
            Debug.Log(data);

            string[] processedUIData = data.Split('.');
            Debug.Log(processedUIData[2]);
            switch (processedUIData[0])
            {
                case "type1": Debug.Log(1); Instantiate(type1, churuataLocation[i].transform).GetComponent<ChuruataPanel>().InstantiateChuruatePannel(processedUIData); break;
                case "type2": Debug.Log(2); Instantiate(type2, churuataLocation[i].transform).GetComponent<ChuruataPanel>().InstantiateChuruatePannel(processedUIData); break;
                case "type3":Debug.Log(3); Instantiate(type3, churuataLocation[i].transform).GetComponent<ChuruataPanel>().InstantiateChuruatePannel(processedUIData); break;
                default: Debug.Log("data is missing"); break;
            }
            i++;
        }  
    }
}
