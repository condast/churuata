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

    [Header(" ")]
    public GameObject prefab;
    public int numberOfObjects = 20;
    public float radius = 5f;
    public float serviceRadius = 3f;
    public float roofHeight = 1.6f;

    public GameObject serviceHolder;
    public GameObject buildingParent;

    public void OnEnable()
    {
        //GameObject GO = new GameObject();
        //var EmptyObject = Instantiate(GO, new Vector3(0, 0, 0), Quaternion.identity);
        //serviceHolder = EmptyObject;
        //serviceHolder.name = "ServiceHolder";
        //serviceHolder.transform.parent = buildingParent.transform;
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
        if(!sManager.useLocalSave)
        sManager.Save(currentService, buildingProgress, buildingID, posX, posY);
    }

    public void AddService()
    {
        foreach (Transform child in serviceHolder.transform)
            GameObject.Destroy(child.gameObject);
        currentService++;
        for (int i = 0; i < currentService; i++)
        {
            float angle = i * Mathf.PI * 2 / currentService;
            float x = Mathf.Cos(angle) * serviceRadius;
            float z = Mathf.Sin(angle) * serviceRadius;
            Vector3 pos = buildingParent.transform.position + new Vector3(x, 0, z);
            float angleDegrees = -angle * Mathf.Rad2Deg;
            Quaternion rot = Quaternion.Euler(0, angleDegrees, 0);
            var serviceHolder = Instantiate(Box, pos, rot);
            serviceHolder.transform.parent = this.serviceHolder.transform;
        }
        int buildingProgress = currentLeafs + currentPillars;
        if (!sManager.useLocalSave)
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
            foreach (Transform child in serviceHolder.transform)
                GameObject.Destroy(child.gameObject);
            currentService++;
            for (int j = 0; j < currentService; j++)
            {
                float angle = j * Mathf.PI * 2 / currentService;
                float x = Mathf.Cos(angle) * serviceRadius;
                float z = Mathf.Sin(angle) * serviceRadius;
                Vector3 pos = this.serviceHolder.transform.position + new Vector3(x, 0, z);
                float angleDegrees = -angle * Mathf.Rad2Deg;
                Quaternion rot = Quaternion.Euler(0, angleDegrees, 0);
                var serviceHolder = Instantiate(Box, pos, rot);
                serviceHolder.transform.parent = this.serviceHolder.transform;
            }
        }
    }

    void createPillar()
    {
        float angle = currentPillars * Mathf.PI * 2 / numberOfObjects;
        float x = Mathf.Cos(angle) * radius;
        float z = Mathf.Sin(angle) * radius;
        Vector3 pos = serviceHolder.transform.position + new Vector3(x, 0, z);
        float angleDegrees = -angle * Mathf.Rad2Deg;
        Quaternion rot = Quaternion.Euler(0, angleDegrees, 0);
        var item = Instantiate(Pillar, pos, rot);
        item.transform.parent = buildingParent.transform;
        item.name = "Pillar - " + currentPillars;
    }

    void createLeaf()
    {
        float angle = currentLeafs * Mathf.PI * 2 / numberOfObjects;
        float x = Mathf.Cos(angle) * radius;
        float z = Mathf.Sin(angle) * radius;
        Vector3 pos = serviceHolder.transform.position + new Vector3(x, roofHeight, z);
        float angleDegrees = -angle * Mathf.Rad2Deg;
        Quaternion rot = Quaternion.Euler(0, angleDegrees, 0);
        var item = Instantiate(Leaf, pos, rot);
        item.transform.parent = buildingParent.transform;
        item.name = "Leaf - " + currentLeafs;
    }
}