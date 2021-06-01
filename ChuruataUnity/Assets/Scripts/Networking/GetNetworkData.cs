using System.Collections;
using UnityEngine;
using UnityEngine.Networking;

public class GetNetworkData : MonoBehaviour
{

    #region Variables

    string adress = "https://www.condast.com:8443/churuatas/rest/walkers/";
    public int clientID = 1;
    public string churuataName;
    public int churuataID = 1;
    public int clientToken = 1;
    public string clientName = "mijnnaam";
    public string clientType = "education";
    public string description = "hellodolly";

    [TextArea(15, 20)]
    public string jsonResponse = "";

    #endregion

    #region Coroutine

    public IEnumerator GetData(string url)
    {
        UnityWebRequest www = UnityWebRequest.Get(url);
        yield return www.SendWebRequest();
        if (www.isNetworkError || www.isHttpError)
        {
            jsonResponse = www.error;
            Debug.LogError(www.error);
        }
        else
        {
            jsonResponse = www.downloadHandler.text;
            Debug.Log(www.downloadHandler.text);
        }
    }

    #endregion

    #region Rest Calls

    public void Select(string _name, float _lat, float _lon, int _range)
    {
        try
        {
            StartCoroutine(GetData(string.Format($"{adress}select?name={_name}@lat={_lat}&lon={_lon}&range={_range}")));
        }
        catch
        {
            throw;
        }
    }

    #region Contributions

    public void RemoveContribution(string _name, int _token, int _churuataID, string _type)
    {
        try
        {
            StartCoroutine(GetData(string.Format($"{adress}")));
        }
        catch
        {
            throw;
        }
    }

    public void Contribute(string _name, int _token, int _churuataID, string _type, string _LOG, string _description)
    {
        try
        {
            string LOG = _LOG.Replace(" ", "%20");
            string description = _description.Replace(" ", "%20");
            StartCoroutine(GetData(string.Format($"{adress}contribute?name={_name}&token={_token}&churuata-id={_churuataID}&type={_type}&contribution={_LOG}&description={description}")));
        }
        catch
        {
            throw;
        }
    }

    public void GetState(string _name, int _token, int _churuataID)
    {
        try
        {
            StartCoroutine(GetData(string.Format($"{adress}get-state?name={_name}&token={_token}&churuata-id={_churuataID}")));
        }
        catch
        {
            throw;
        }
    }

    #endregion

    #region Presentations

    public void AddPresentation(string _name, int _token, int _churuataID, string _type, string _title, string _description, string _link)
    {
        try
        {
            string title = _title.Replace(" ", "%20");
            string description = _description.Replace(" ", "%20");
            StartCoroutine(GetData(string.Format($"{adress}add-presentation?name={_name}&token={_token}&churuata-id={_churuataID}&type={_type}&title={_name}&description={_description}&link={_link}")));
        }
        catch
        {
            throw;
        }
    }

    public void RemovePresentation(string _name, int _token, int _churuataID, string _title)
    {
        try
        {
            string title = _title.Replace(" ", "%20");
            StartCoroutine(GetData(string.Format($"{adress}remove-presentation?name={_name}&token={_token}&churuata-id={_churuataID}&title={title}")));
        }
        catch
        {
            throw;
        }
    }

    public void GetVideos(string _name, int _token, int _churuataID)
    {
        try
        {
            StartCoroutine(GetData(string.Format($"{adress}get-videos?name={_name}&token={_token}&churuata-id={_churuataID}")));
        }
        catch
        {
            throw;
        }
    }

    public void GetHammocks(string _name, int _token, int _churuataID)
    {
        try
        {
            StartCoroutine(GetData(string.Format($"{adress}get-hammocks?name={_name}&token={_token}&churuata-id={_churuataID}")));
        }
        catch
        {
            throw;
        }
    }

    #endregion

    #region Murmers

    public void AddMurmering(string _name, int _token, int _churuataID, string _text)
    {
        try
        {
            string text = _text.Replace(" ", "%20");
            StartCoroutine(GetData(string.Format($"{adress}add-murmering?name={_name}&token={_token}&churuata-id={_churuataID}&text={text}")));
        }
        catch
        {
            throw;
        }
    }

    public void RemoveMurmering(string _name, int _token, int _churuataID, string _filter)
    {
        try
        {
            string filter = _filter.Replace(" ", "%20");
            StartCoroutine(GetData(string.Format($"{adress}remove-presentation?name={_name}&token={_token}&churuata-id={_churuataID}&filter={filter}")));
        }
        catch
        {
            throw;
        }
    }

    public void GetMurmering(string _name, int _token, int _churuataID)
    {
        try
        {
            StartCoroutine(GetData(string.Format($"{adress}get-murmerings?name={_name}&token={_token}&churuata-id={_churuataID}")));
        }
        catch
        {
            throw;
        }
    }

    #endregion

    #endregion
}
