using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ChuruataPanel : MonoBehaviour
{
    public Text textPanel;
    public string churuataType;

    public void SetText(string text)
    {
        Debug.Log(text);
        textPanel.text = text;
    }
    public string GetChuruataType() { return churuataType; }
}
