using TMPro;
using UnityEngine;
using UnityEngine.UI;

public class NewData : MonoBehaviour
{
    public TextMeshProUGUI dataText;

    public void NewDatas()
    {
        RestCallTest j = APIHelper.rest();
        dataText.text = j.select;
    }
}
