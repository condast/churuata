using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Networking;


public class Builder : MonoBehaviour
{
    
    public string rawData;
    public List<string> procesedData;
    public WWWForm wwwForm;
    //
    public string dbURL = " https://www.condast.com:8443/churuatas/rest/walkers/select?name=keesp&lat=52&lon=6.5&range=10000";
    public List<GameObject> locationList;
    public GameObject empty;
    public GameObject palm;
    public GameObject leaves;
    public ChuruateTypeHandler handler;
    
    void Start()
    {
        Debug.Log(1);
        StartCoroutine(OnStart());
        // ConstructList();
        UseData();
    }

    IEnumerator OnStart()
    {
        WWW data = new WWW(dbURL);
        yield return data;
            Debug.Log(data.text);
            rawData = data.text;

    }
    private void ConstructList()
    {
        foreach(string data in procesedData)
        {
            GameObject x =Instantiate(empty,this.transform);            
            string[] saveLock;
            saveLock = data.Split(char.Parse("|"));
            for(int i =0;i < Int32.Parse(saveLock[1]) ; i++)
            {
                Instantiate(palm, x.transform);
                Debug.Log(i);
            }
            for (int i = 0; i < Int32.Parse(saveLock[2]); i++)
            {
                Instantiate(leaves, x.transform);
                Debug.Log(i);
            }
        }
    }
    void UseData()
    {
        string[] data = rawData.Split('/');
        handler.GiveData(data);
    }
}
