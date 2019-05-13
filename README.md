# ycb-tool
The YellowCartonBox video preparation tooling


## Build

```bash
./gradlew build

```

## Run
```bash
java -jar build/libs/ycb-tool-all.jar <parameters>
```

## Local usage note

```
Usage:
  java -jar ycb-tool-all.jar [command] [args]

Commands:
  - init   - creates a new project (directory and necessary initial files).
  - update - *default* looks for project files in the current directory and
             does overall project re-evaluation.
  - help   - prints this message.

Project structure:
  Labels.
    All label are supposed to be indicated by &&labels.labelName pattern (e.g. &&labels.mainTitle).

  Tracks.
    Edition depending tracks are indicated by __edition suffix (e.g. NarrativeAudioTrack_en).
```
