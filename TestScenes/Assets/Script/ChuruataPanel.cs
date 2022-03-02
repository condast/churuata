using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ChuruataPanel : MonoBehaviour
{
    public Text textPanel;
    public GameObject asset;
    
    public virtual void InstantiateChuruatePannel(string[] saveLock)
    {
        Debug.Log("started");
        SetText(saveLock[1]);
        CreateAssets(saveLock[2]);
    }

    public void SetText(string text)
    {
        textPanel.text = text;
    }
    public void CreateAssets(string ammountString)
    {
        int amount = int.Parse(ammountString);
        for(int i = 1;i <= amount; i++)
        {
            Instantiate(asset, this.transform);
        }
    }
}
