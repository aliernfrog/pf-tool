> [!WARNING]
> App may not work on devices using **October 2023 or newer security patches**, as those security patches prevent apps from requesting access to files inside `Android/data` folder.
> 
> If you are affected by this, please use the following methods until a solution is found:
> - Connect your device to a PC with USB or use wireless debugging, you'll be able to manually manage map files inside `Android/data/com.MA.Polyfield/files/editor`
> - Download maps using in-game map list

<div align="center">

  <img alt="PF Tool icon" src="images/icon.png" width="120px"/>
  
  # PF Tool
  Easily import and share <a href="https://play.google.com/store/apps/details?id=com.MA.Polyfield">Polyfield</a> maps

  <br>

  [![Download](https://img.shields.io/github/v/tag/aliernfrog/pf-tool?style=for-the-badge&label=Download)](https://github.com/aliernfrog/pf-tool/releases/latest/download/pftool.apk)

  <br>

  ![Download count](https://img.shields.io/github/downloads/aliernfrog/pf-tool/total?style=for-the-badge&label=Download%20Count)
  ![Build status](https://img.shields.io/github/actions/workflow/status/aliernfrog/pf-tool/commit.yml?style=for-the-badge&label=Build%20status)

  ---
  
  <img alt="PF Tool screenshot" src="images/maps.jpg" width="200px"/>
  
</div>

## 🔧 Building
- Clone the repository
- Do your changes
- `./gradlew assembleRelease`
