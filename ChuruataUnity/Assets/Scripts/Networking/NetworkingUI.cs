using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class NetworkingUI : MonoBehaviour
{
    public GetNetworkData networkData;

    [SerializeField] int clientID = 1;
    [SerializeField] int churuataID = 1;
    [SerializeField] int clientToken = 1;
    [SerializeField] string clientName = "mijnnaam";
    [SerializeField] string clientType = "education";
    [SerializeField] string description = "hellodolly";

    public TMP_InputField clientIDInput;
    public TMP_InputField churuataIDInput;
    public TMP_InputField clientTokenInput;
    public TMP_InputField clientNameInput;
    public TMP_InputField clientTypeInput;
    public TMP_InputField descriptionInput;

    public GameObject UICanvas;
    public GameObject BuildCanvas;
    public bool canvasEnabled = false;

    public Text returnText;

    public void OnEnable()
    {
        UICanvas.SetActive(canvasEnabled);
        BuildCanvas.SetActive(!canvasEnabled);
        networkData = GetComponent<GetNetworkData>();
    }

    public void Update()
    {
        Int32.TryParse(clientIDInput.text, out clientID);
        Int32.TryParse(churuataIDInput.text, out churuataID);
        //Int32.TryParse(clientTokenInput.text, out clientToken);
        clientName = clientNameInput.text;
        //clientType = clientTypeInput.text;
        description = descriptionInput.text;
        returnText.text = networkData.jsonResponse;
    }

    public void Register()
    {
        networkData.Register(clientID, clientToken, clientName, clientType);
    }

    public void GetChuruata()
    {
        networkData.GetChuruata(clientID, clientToken, churuataID);
    }

    public void Contribute()
    {
        networkData.Contribute(clientID, clientToken, clientType, description);
    }

    public void TestBuilding()
    {
        canvasEnabled = !canvasEnabled;
        UICanvas.SetActive(canvasEnabled);
        BuildCanvas.SetActive(!canvasEnabled);
    }

    public void Login()
    {
        
    }
}
