using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ChuruataController : MonoBehaviour
{

    public Canvas sleepLocation;
    public Canvas legalLocation;
    public Canvas eatLocation;


    public void CreayChuruata(string idk)
    {
        switch (idk)
        {
            case "sleep": 
                Instantiate(sleepLocation); 
                break;
            case "legal": 
                Instantiate(legalLocation); 
                break;
            case "eat": 
                Instantiate(eatLocation);
                break;
            default: Debug.Log(idk); break;
        }

    }
}
