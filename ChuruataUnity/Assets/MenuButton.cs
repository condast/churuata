using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class MenuButton : MonoBehaviour
{
    public GameObject panel;
    public GameObject thisPanel;

    public void OpenPanel()
    {
        panel.SetActive(true);
        thisPanel.SetActive(false);
    }
    public void ClosePanel()
    {
        panel.SetActive(false);
        thisPanel.SetActive(true);
    }
}
