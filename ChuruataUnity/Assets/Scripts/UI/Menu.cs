using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class Menu : MonoBehaviour
{
    public GameObject menuObject;
    bool openMenu;

    public void ExitApp()
    {
        Application.Quit();
    }

    public void OpenScene(int SceneToOpen)
    {
        SceneManager.LoadScene(SceneToOpen);
    }

    public void OpenMenu()
    {
        openMenu = !openMenu;
        menuObject.SetActive(openMenu);
    }
}
