using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net;
using System;
using System.IO;
using UnityEngine.Networking;

public class GetNetworkData : MonoBehaviour
{
    string adress = "http://www.condast.com:8080/churuata/rest/";

    HttpWebRequest request;
    HttpWebResponse response;

    public int clientID = 1;
    public int churuataID = 1;
    public int clientToken = 1;
    public string clientName = "mijnnaam";
    public string clientType = "education";
    public string description = "hellodolly";

    [TextArea(15, 20)]
    public string jsonResponse = "";

    public void Register(int clientID, int clientToken, string clientname, string clientType)
    {
        Debug.Log("Trying to register to the database");
        try
        {
            string url = string.Format("{0}find?userid={1}&token={2}&name={3}&type={4}", adress, clientID, clientToken, clientname, clientType);
            StartCoroutine(GetData(url));
            Debug.Log("Getting data was succesfull!");
        }
        catch
        {
            Debug.LogError("Getting data was unsuccesfull!");
            throw;
        }
    }
    public void GetChuruata(int clientID, int clientToken, int churuataID)
    {
        Debug.Log("Trying to get churuata from the database");
        try
        {
            string url = string.Format("{0}find?userid={1}&token={2}&id={3}", adress, clientID, clientToken, churuataID);
            StartCoroutine(GetData(url));
            Debug.Log("Getting data was succesfull!");
        }
        catch
        {
            throw;
        }
    }

    IEnumerator GetData(string url)
    {
        UnityWebRequest www = UnityWebRequest.Get(url);
        yield return www.SendWebRequest();
        if (www.isNetworkError || www.isHttpError)
        {
            jsonResponse = www.error;
            Debug.Log(www.error);
        }
        else
        {
            jsonResponse = www.downloadHandler.text;
            Debug.Log(www.downloadHandler.text);
        }
    }

    public void Contribute(int clientID, int token, string type, string description)
    {
        Debug.Log("Trying to get churuata from the database");
        try
        {
            string url = string.Format("{0}add-contribution?userid={1}&token={2}&type={3}&description={4}&contribution=log", adress, clientID, token, type, description);
            StartCoroutine(GetData(url));
            Debug.Log("Getting data was succesfull!");
        }
        catch
        {
            throw;
        }
    }
}
