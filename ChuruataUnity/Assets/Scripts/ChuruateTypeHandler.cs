using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Text;
using UnityEngine.UI;

public class ChuruateTypeHandler : MonoBehaviour
{
    public string[] churuataData;
    public ChuruataPanel[] churuatepanels;
    public Canvas UI;

    void Start()
    {
        GiveData(churuataData);
    }

    public void GiveData(string[] data)
    {
        churuataData = data;
        UseData(data);
    }

    // Start is called before the first frame update
    void UseData(string[] churuataData)
    {
        foreach (string data in churuataData)
        {
            string[] splitData = data.Split('.');
            foreach(ChuruataPanel panel in churuatepanels)
            {
                if(splitData[0] == panel.GetChuruataType())
                {
                    panel.SetText(splitData[1]);
                }
            }
        }
    }
}
