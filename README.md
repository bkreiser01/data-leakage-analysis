# DataLeakage_Fall2023

<!-- Plugin description -->
First version of a plugin that detects data leakage in Python code for ML.
<!-- Plugin description end -->

## Installation

- **<ins>Prerequisite:</ins>**
  Please have Docker **INSTALLED** and **RUNNING BEFORE** you run the Data Leakage Plugin. Links to the documents explaining how to install Docker and videos down below. 

  **INSTALLING DOCKER**
  </br>
  Docker is available for Linux, MacOS, and Windows, making it something very useful for developers who run into the ‘but it works on my machine’ issue. It can also allow you to create a container, and anyone can run it on nearly any device with no care about the operating system. For all of the installations we will be using a package manager.

  **WINDOWS INSTALLATION**
  </br>
  There are many ways to install docker on Windows, but we will be using Chocolatey, a package manager for Windows. If you dont have it, please install it by following the steps outlined here: https://chocolatey.org/install
  
  In an administrator powershell console, run the command.There may be a confirmation request in the console, confirm it or the install will halt.

  ```
  choco install docker-desktop
  ```

  Alternatively follow the installation instructions from the Docker Desktop Homepage: https://www.docker.com/products/docker-desktop/

  **MAC INSTALLATION**
  </br>
  We are gonna be using Homebrew for this, so install it if you haven't already. All you need to do is run the following command in your terminal if you have homebrew installed.

  ```
  brew install --cask docker
  ```

  Alternatively follow the installation instructions from the Docker Desktop Homepage: https://www.docker.com/products/docker-desktop/

  **RUNNING THE DOCKER DESKTOP**
  </br>
  For Windows and MacOS you need to run the Docker Desktop application that was installed to your local computer during the above steps. You need to do this to start the docker engine. Go through the recommended setup options (there is no need to create an account right now, skip where you can)

  **VERIFY YOUR INSTALL**
  </br>
  In order to verify your install, run the command 
  ```
  docker run hello-world 
  ```
  if you get an error message, you have not properly installed docker.
  
  **Note:** You will most likely need to restart your CLI in order to use the command after installation. 


  **Documents:**
  https://docs.google.com/document/d/e/2PACX-1vTyDBH3hAODbK_PtPLiX7hWej7_6cViKnEHTLkL0jpLS11LKeUMEdWx3pvfdZtYIA/pub

  **Installing Docker on Mac Video (first half):**
  https://www.youtube.com/watch?v=3fiYHaaz5eQ

  **Installing Docker on Windows 11 Video:**
  https://www.youtube.com/watch?v=8qQeODFSPQ4
  
- **<ins>Data Leakage Plugin Download:</ins>**
  Please follow the directions on the documents and the videos in order to download the plugin. Instructions provided for both Mac and Windows OS Systems. 

  **Documents:**
  https://docs.google.com/document/d/e/2PACX-1vRqhcW4GnHS8JnSI6x44Pd7270obvbCLo8UBrP6nT8a_lNS2tmD6b5dC4sWDNb2lQ/pub

  **Installation for Mac (second half of the video):**
  https://www.youtube.com/watch?v=3fiYHaaz5eQ

  **Installation for Windows 11 Video:**
  https://www.youtube.com/watch?v=l-dHJxA3S0c

## Usage

- **<ins>Data Leakage Plugin Usage:</ins>**
  Video down below shows how to run the data leakage plugin for a given python file in pycharm. 
  https://youtu.be/gb9sF-MTsM8
