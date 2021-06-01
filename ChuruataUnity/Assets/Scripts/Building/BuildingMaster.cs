using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BuildingMaster : MonoBehaviour
{
    [Header("Script References")]
    public SaveManager sManager;
    public GetNetworkData getNetworkData;

    [Header("Prefabs")]
    public GameObject Pillar;
    public GameObject Leaf;
    public GameObject Box;
    public GameObject TVObject;

    public int currentPillars;
    public int currentLeafs;
    public int currentService;

    [Header("Settings")]
    public int buildingID;
    public string buildingName;
    public int buildingToken;
    public float posX;
    public float posY;
    public int numberOfObjects = 20;
    public float pillarRadius = 5f;
    public float serviceRadius = 3f;
    public float roofHeight = 1.6f;
    public int currentTV;
    public int TVCount;
    public int TVRadius;
    public List<GameObject> TVS;
    public List<string> videoURLs;

    [Header("Parents")]
    public GameObject serviceHolder;
    public GameObject buildingParent;
    public GameObject TVParent;



    [Header("Test Variables")]
    public bool demoTent = false;
    public float delayCount = 5;

    private void OnEnable()
    {
        if (demoTent)
        {
            StartCoroutine("Delay");
        }
    }

    public IEnumerator Delay()
    {
        Debug.Log("starting timer");
        yield return new WaitForSecondsRealtime(delayCount);
        Debug.Log("building tent");
        for (int i = 0; i < numberOfObjects * 2; i++)
        {
            AddPart();
        }
    }

    void GetVideos()
    {
        getNetworkData.GetVideos(buildingName, buildingToken, buildingID);
        videoURLs.Add(getNetworkData.jsonResponse);
    }

    public void AddPart()
    {
        if (currentPillars != numberOfObjects)
        {
            currentPillars++;
            createPillar();
        }
        if (currentPillars == numberOfObjects && currentLeafs != numberOfObjects)
        {
            currentLeafs++;
            createLeaf();
        }
        else if (currentPillars == numberOfObjects && currentLeafs == numberOfObjects)
        {
            GetVideos();
            createTV();
        }
        int _buildingProgress = currentLeafs + currentPillars;
        if (!sManager.useLocalSave)
            sManager.Save(currentService, _buildingProgress, buildingID, posX, posY);
    }

    public void AddService()
    {
        foreach (Transform child in serviceHolder.transform)
            GameObject.Destroy(child.gameObject);
        currentService++;
        for (int i = 0; i < currentService; i++)
            createLoop(i, currentService, Box, serviceHolder, serviceRadius, "Service - ", 0);
    }

    void createTV()
    {
        for (int i = 0; i < videoURLs.Count; i++)
            createLoop(i, TVCount, TVObject, TVParent, TVRadius, "TV - ", 0);
        int j = 0;
        foreach (Transform child in TVParent.transform)
        {
            GameObject TV = child.GetComponent<GameObject>();
            TVS.Add(TV);
            TV.GetComponentInChildren<YoutubeSimplified>().url = videoURLs[j];
            TV.GetComponentInChildren<YoutubeSimplified>().Play();
            j++;
        }
    }

    void createPillar()
    {
        createLoop(currentPillars, numberOfObjects, Pillar, buildingParent, pillarRadius, "Pillar - ", 0);
    }

    void createLeaf()
    {
        createLoop(currentLeafs, numberOfObjects, Leaf, buildingParent, pillarRadius, "Leaf - ", roofHeight);
    }

    // The loop for instantiating the gameobject in a certain radius a given time
    // Object count is how many of the object there already are
    // NumberOfObjects is how many of there should be in the end
    // objectToInstantiate is which gameObject there will be instantiated
    // objectParent is which gameObject will be the parent of the objectToInstantiate
    // radius is in whatever radius the circle will be
    // objectName is the name given to the object + the count of the object. FE; Pillar - 1
    // objectHeight is the height the object will instantiate, if not necesarry enter 0

    void createLoop(int _objectCount, int _numberOfObjects, GameObject _objectToInstantiate, GameObject _objectParent, float _radius, string _objectName, float _objectHeight)
    {
        float angle = _objectCount * Mathf.PI * 2 / _numberOfObjects;
        float x = Mathf.Cos(angle) * _radius;
        float z = Mathf.Sin(angle) * _radius;
        Vector3 pos = _objectParent.transform.position + new Vector3(x, _objectHeight, z);
        float angleDegrees = -angle * Mathf.Rad2Deg;
        Quaternion rot = Quaternion.Euler(0, angleDegrees, 0);
        var objectHolder = Instantiate(_objectToInstantiate, pos, rot);
        objectHolder.transform.parent = _objectParent.transform;

        // Optional stuff, setting name of gameObject
        if (_objectName != string.Empty)
            objectHolder.name = _objectName + " " + _objectCount;
    }

    public void loadBuildingProgress(int _buildingProgress, int _serviceCount)
    {
        for (int i = 0; i < _buildingProgress; i++)
        {
            if (currentPillars != numberOfObjects)
            {
                currentPillars++;
                createPillar();
            }
            if (currentPillars == numberOfObjects && currentLeafs != numberOfObjects)
            {
                currentLeafs++;
                createLeaf();
            }
        }
        for (int i = 0; i < _serviceCount; i++)
        {
            foreach (Transform child in serviceHolder.transform)
                GameObject.Destroy(child.gameObject);
            currentService++;
            for (int j = 0; j < currentService; j++)
                createLoop(j, currentService, Box, serviceHolder, serviceRadius, "Service - ", 0);
        }
    }
}