using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Video;

public class TVRendering : MonoBehaviour
{
    public VideoPlayer vidPlayer;
    public VideoClip vidClip;

    public void OnEnable()
    {
        vidPlayer.clip = vidClip;
        vidPlayer.Play();
        vidPlayer.isLooping = true;
    }
}
