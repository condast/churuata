using System.Collections;
using UnityEngine;
using UnityEngine.Networking;

public class GetNetworkData : MonoBehaviour
{
    string adress = "http://www.condast.com:8080/churuata/rest/";
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
        try
        {
            StartCoroutine(GetData(string.Format("{0}find?userid={1}&token={2}&name={3}&type={4}", adress, clientID, clientToken, clientname, clientType)));
        }
        catch
        {
            throw;
        }
    }
    public void GetChuruata(int clientID, int clientToken, int churuataID)
    {
        try
        {
            StartCoroutine(GetData(string.Format("{0}find?userid={1}&token={2}&id={3}", adress, clientID, clientToken, churuataID)));
        }
        catch
        {
            throw;
        }
    }

    public IEnumerator GetData(string url)
    {
        UnityWebRequest www = UnityWebRequest.Get(url);
        yield return www.SendWebRequest();
        if (www.isNetworkError || www.isHttpError)
            jsonResponse = www.error;
        else
            jsonResponse = www.downloadHandler.text;
    }

    public void Contribute(int clientID, int token, string type, string description)
    {
        try
        {
            StartCoroutine(GetData(string.Format("{0}add-contribution?userid={1}&token={2}&type={3}&description={4}&contribution=log", adress, clientID, token, type, description)));
        }
        catch
        {
            throw;
        }
    }

    public void RegisterAccount(string username, string emailAdress, string password)
    {
        try
        {
            //Make register web link, should return an "Okay" signal if the creation was succesfull, and should be able to login afterwards
            //string url = string.Format("{0}add-contribution?userid={1}&token={2}&type={3}&description={4}&contribution=log", adress, clientID, token, type, description);
            
            
            //StartCoroutine(GetData(url));
        }
        catch
        {
            throw;
        }
    }

    public void LoginAccount(string username, int clientID, string message)
    {
        try
        {
            //string url = string.Format("{0}add-contribution?userid={1}&token={2}&type={3}&description={4}&contribution=log", adress, clientID, token, type, description);


            //StartCoroutine(GetData(url));
        }
        catch
        {
            throw;
        }
    }

    public void SendMessage(string username, int clientID, string message)
    {
        try
        {
            //string url = string.Format("{0}add-contribution?userid={1}&token={2}&type={3}&description={4}&contribution=log", adress, clientID, token, type, description);


            //StartCoroutine(GetData(url));
        }
        catch
        {
            throw;
        }
    }

    public void ReceiveMessage(string emailAdress, string password)
    {
        try
        {
            //string url = string.Format("{0}add-contribution?userid={1}&token={2}&type={3}&description={4}&contribution=log", adress, clientID, token, type, description);


            //StartCoroutine(GetData(url));
        }
        catch
        {
            throw;
        }
    }
}
