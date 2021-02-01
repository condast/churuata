using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System;

public class SaveManager : MonoBehaviour
{
    int buildingID;
    int buildingProgress;
    int serviceCount;
    float posX;
    float posY;

    public GameObject building;

    string path;

    public bool load;
    public bool createBuilding;

    int lastCreatedBuilding = 0;

    public void OnEnable()
    {
        path = Application.persistentDataPath + "/Saves/";
        if (!Directory.Exists(path))
            Directory.CreateDirectory(path);
        Load();
    }

    public void Update()
    {
        if (load)
        {
            load = false;
            Load();
        }
        if (createBuilding)
        {
            createBuilding = false;
            CreateBuilding();
        }
    }

    public void CreateBuilding()
    {
        lastCreatedBuilding++;
        BuildingMaster bMaster;
        var Building = Instantiate(building);
        Building.name = "Building - " + lastCreatedBuilding;
        bMaster = Building.GetComponent<BuildingMaster>();
        bMaster.sManager = this;
        bMaster.buildingID = lastCreatedBuilding;
        Save(serviceCount, buildingProgress, lastCreatedBuilding, 0, 0);
    }

    public void Save(int serviceCount, int buildingProgress, int buildingID, float posX, float posY)
    {
        string savePath = path + "Save" + buildingID + ".txt";
        if (!File.Exists(savePath))
        {
            FileStream fs = File.Create(savePath);
            StreamWriter fw = new StreamWriter(fs);
            fw.WriteLine(buildingID);
            fw.WriteLine(buildingProgress);
            fw.WriteLine(serviceCount);
            fw.WriteLine(posX);
            fw.WriteLine(posY);
            fw.Flush();
            fw.Close();
            return;
        }
        if (File.Exists(savePath))
        {
            File.WriteAllText(savePath, String.Empty);
            FileStream fs = File.Open(savePath, FileMode.Append, FileAccess.Write);
            StreamWriter fw = new StreamWriter(fs);
            fw.WriteLine(buildingID);
            fw.WriteLine(buildingProgress);
            fw.WriteLine(serviceCount);
            fw.WriteLine(posX);
            fw.WriteLine(posY);
            fw.Flush();
            fw.Close();
            return;
        }
    }

    public void Load()
    {
        string savePath = path + "Save" + buildingID + ".txt";

        string[] SaveDataList = Directory.GetFiles(path);
        for (int i = 0; i < SaveDataList.Length; i++)
        {
            if (!File.Exists(SaveDataList[i]))
                return;
            if (File.Exists(SaveDataList[i]))
            {
                string[] saveDataText = File.ReadAllLines(SaveDataList[i]);
                Int32.TryParse(saveDataText[0], out buildingID);
                Int32.TryParse(saveDataText[1], out buildingProgress);
                Int32.TryParse(saveDataText[2], out serviceCount);
                posX = float.Parse(saveDataText[3]);
                posY = float.Parse(saveDataText[4]);
                if (lastCreatedBuilding <= buildingID)
                    lastCreatedBuilding = buildingID;
                BuildingMaster bMaster;
                var Building = Instantiate(building);
                Building.name = "Building - " + buildingID.ToString().PadLeft(5, '0');
                bMaster = Building.GetComponent<BuildingMaster>();
                bMaster.sManager = this;
                bMaster.buildingID = buildingID;
                bMaster.posX = posX;
                bMaster.posY = posY;
                bMaster.loadBuildingProgress(buildingProgress, serviceCount);
            }
        }
    }
}
