using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class NetworkingUI : MonoBehaviour
{
    public GetNetworkData networkData;

    [SerializeField] int ID = 1;
    [SerializeField] int Token = 1;
    [SerializeField] string Name = "mijnnaam";
    [SerializeField] string Title = "title";
    [SerializeField] string Type = "education";
    [SerializeField] string Description = "hellodolly";
    [SerializeField] string URL = "videoURL";
    [SerializeField] float Lat = 7;
    [SerializeField] float Lon = 7;
    [SerializeField] int Range = 5;
    [SerializeField] string LOG = "LOG";

    public TMP_InputField IDInput;
    public TMP_InputField TokenInput;
    public TMP_InputField NameInput;
    public TMP_InputField TitleInput;
    public TMP_InputField TypeInput;
    public TMP_InputField DescriptionInput;
    public TMP_InputField URLInput;
    public TMP_InputField LonInput;
    public TMP_InputField LatInput;
    public TMP_InputField RangeInput;
    public TMP_InputField LOGInput;

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
        Int32.TryParse(IDInput.text, out ID);
        Name = NameInput.text;
        Title = TitleInput.text;
        Type = TypeInput.text;
        Description = DescriptionInput.text;
        URL = URLInput.text;
        float.TryParse(LonInput.text, out Lon);
        float.TryParse(LatInput.text, out Lat);
        int.TryParse(RangeInput.text, out Range);
        LOG = LOGInput.text;

        returnText.text = networkData.jsonResponse;
    }

    public void Contribute()
    {
        networkData.Contribute(Name, Token, ID, Type, $"Added Type {Type}", $"Added a {Type} to the churuata");
    }

    public void GetChuruata()
    {
        networkData.GetState(Name, Token, ID);
    }

    public void GetHammocks()
    {
        networkData.GetHammocks(Name, Token, ID);
    }

    public void TestBuilding()
    {
        canvasEnabled = !canvasEnabled;
        UICanvas.SetActive(canvasEnabled);
        BuildCanvas.SetActive(!canvasEnabled);
    }

    public void SelectChuruata()
    {
        networkData.Select(Name, Lat, Lon, Range);
    }

    public void RemoveContribution()
    {
        networkData.RemoveContribution(Name, Token, ID, Type);
    }

    public void UploadVideoURL()
    {
        networkData.AddPresentation(Name, Token, ID, "VIDEO", Title, Description, URL);
    }

    public void DeleteVideo()
    {
        networkData.RemovePresentation(Name, Token, ID, Title);
    }

    public void GetVideos()
    {
        networkData.GetVideos(name, Token, ID);
    }
}
