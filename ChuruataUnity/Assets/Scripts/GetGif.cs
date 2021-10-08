using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System;
using UnityEngine.Networking;
using System.Data;

public class GetGif : MonoBehaviour
{
    public Text text;

    public string[] comments;
    public string requestUrl = "https://localhost/churuata/test.php";
    

    void Start()
    {

        SetText();
        
    }
    public IEnumerator Retrieve()
    {

        using (UnityWebRequest webRequest = UnityWebRequest.Get(requestUrl))
        {
            // Request and wait for the desired page.
            yield return webRequest.SendWebRequest();
            if (webRequest.isNetworkError)
            {
                Debug.Log("Error: " + webRequest.error);
            }
            else
            {
                Debug.Log("Received: " + webRequest.downloadHandler.text);
                string fulldata = webRequest.downloadHandler.text;
                comments = fulldata.Split(new string[] { "<br>" }, StringSplitOptions.None);
                Debug.Log(String.Format("There are {0} comments.", comments.Length));
                foreach (string comment in comments)
                {
                    Debug.Log(comment);
                }
            }
        }
    }

     IEnumerator SetText()
    {
        Debug.Log("Start Connecting");
        WWW dataCenter = new WWW(requestUrl);
        yield return dataCenter;
        if (dataCenter.text == "0")
        {
            Debug.Log("connection succes");
            text.text = dataCenter.text;
        }
        else
        {
            Debug.Log("Connection Failed");
        }

    }

}