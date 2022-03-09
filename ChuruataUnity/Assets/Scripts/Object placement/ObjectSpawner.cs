using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ObjectSpawner : MonoBehaviour
{
    public GameObject cube;
    public GameObject churuata;
    public GameObject objectToSpawn;
    private PlacementIndicator placementIndicator;
    private GameObject LastPlacedObj;

    private void Start()
    {
        placementIndicator = FindObjectOfType<PlacementIndicator>();

    }

    private void Update()
    {

        if (Input.touchCount > 0 && Input.touches[0].phase == TouchPhase.Began)
        {
            GameObject obj = Instantiate(objectToSpawn, placementIndicator.transform.position, placementIndicator.transform.rotation);
            LastPlacedObj = obj;
        }
    }

    public void ChangeCube()
    {
        objectToSpawn = cube;
    }

    public void ChangeChuruata()
    {
        objectToSpawn = churuata;
    }

    public void ChangeSizeUp()
    {
        LastPlacedObj.transform.localScale += new Vector3(1, 1, 1);
    }

    public void ChangeSizeDown() 
    {
        LastPlacedObj.transform.localScale -= new Vector3(-1, -1, -1);
    }


}
