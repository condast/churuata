using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class MainMenu : MonoBehaviour
{
    public GameObject RegisterCanvas;
    public GameObject LoginCanvas;

    public TMP_InputField passwordLogin;
    public TMP_InputField emailLogin;

    public TMP_InputField usernameCreate;
    public TMP_InputField passwordCreate;
    public TMP_InputField emailCreate;

    public string username;
    public string password;
    public string email;

    bool isLoggingIn = true;

    public void Update()
    {
        if (isLoggingIn)
        {
            password = passwordLogin.text;
            email = emailLogin.text;
        }
        else if (!isLoggingIn)
        {
            username = usernameCreate.text;
            password = passwordCreate.text;
            email = emailCreate.text;
        }
    }

    public void CreateAccount()
    {
        LoginCanvas.SetActive(false);
        RegisterCanvas.SetActive(true);
        isLoggingIn = false;
    }

    public void ReturnToLogin()
    {
        LoginCanvas.SetActive(true);
        RegisterCanvas.SetActive(false);
        isLoggingIn = true;
    }
}
