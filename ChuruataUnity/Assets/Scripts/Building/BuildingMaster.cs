using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BuildingMaster : MonoBehaviour
{
    public int buildingID;
    public float posX;
    public float posY;
    public SaveManager sManager;

    public GameObject Pillar;
    public GameObject Leaf;
    public GameObject Box;

    public int currentPillars;
    public int currentLeafs;
    public int currentService;

    [Header("Test Buildings")]
    public bool TestBuilding;
    public bool TestService;
    [Header(" ")]
    public GameObject prefab;
    public int numberOfObjects = 20;
    public float radius = 5f;
    public float serviceRadius = 3f;
    public float roofHeight = 1.6f;

    public GameObject ServiceHolder;

    void Update()
    {
        if (TestBuilding)
        {
            TestBuilding = false;
            AddPart();
        }
        if (TestService)
        {
            TestService = false;
            AddService();
        }
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
        int buildingProgress = currentLeafs + currentPillars;
        sManager.Save(currentService, buildingProgress, buildingID, posX, posY);
    }

    public void AddService()
    {
        foreach (Transform child in ServiceHolder.transform)
            GameObject.Destroy(child.gameObject);
        currentService++;
        for (int i = 0; i < currentService; i++)
        {
            float angle = i * Mathf.PI * 2 / currentService;
            float x = Mathf.Cos(angle) * serviceRadius;
            float z = Mathf.Sin(angle) * serviceRadius;
            Vector3 pos = transform.position + new Vector3(x, 0, z);
            float angleDegrees = -angle * Mathf.Rad2Deg;
            Quaternion rot = Quaternion.Euler(0, angleDegrees, 0);
            var serviceHolder = Instantiate(Box, pos, rot);
            serviceHolder.transform.parent = ServiceHolder.transform;
        }
        int buildingProgress = currentLeafs + currentPillars;
        sManager.Save(currentService, buildingProgress, buildingID, posX, posY);
    }

    public void loadBuildingProgress(int buildingProgress, int serviceCount)
    {
        for (int i = 0; i < buildingProgress; i++)
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
        for (int i = 0; i < serviceCount; i++)
        {
            foreach (Transform child in ServiceHolder.transform)
                GameObject.Destroy(child.gameObject);
            currentService++;
            for (int j = 0; j < currentService; j++)
            {
                float angle = j * Mathf.PI * 2 / currentService;
                float x = Mathf.Cos(angle) * serviceRadius;
                float z = Mathf.Sin(angle) * serviceRadius;
                Vector3 pos = transform.position + new Vector3(x, 0, z);
                float angleDegrees = -angle * Mathf.Rad2Deg;
                Quaternion rot = Quaternion.Euler(0, angleDegrees, 0);
                var serviceHolder = Instantiate(Box, pos, rot);
                serviceHolder.transform.parent = ServiceHolder.transform;
            }
        }
    }

    void createPillar()
    {
        float angle = currentPillars * Mathf.PI * 2 / numberOfObjects;
        float x = Mathf.Cos(angle) * radius;
        float z = Mathf.Sin(angle) * radius;
        Vector3 pos = transform.position + new Vector3(x, 0, z);
        float angleDegrees = -angle * Mathf.Rad2Deg;
        Quaternion rot = Quaternion.Euler(0, angleDegrees, 0);
        var item = Instantiate(Pillar, pos, rot);
        item.transform.parent = this.transform;
        item.name = "Pillar - " + currentPillars;
    }

    void createLeaf()
    {
        float angle = currentLeafs * Mathf.PI * 2 / numberOfObjects;
        float x = Mathf.Cos(angle) * radius;
        float z = Mathf.Sin(angle) * radius;
        Vector3 pos = transform.position + new Vector3(x, roofHeight, z);
        float angleDegrees = -angle * Mathf.Rad2Deg;
        Quaternion rot = Quaternion.Euler(0, angleDegrees, 0);
        var item = Instantiate(Leaf, pos, rot);
        item.transform.parent = this.transform;
        item.name = "Leaf - " + currentLeafs;
    }
}