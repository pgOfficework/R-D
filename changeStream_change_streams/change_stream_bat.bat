@echo off
title changestreams
java -cp target/libs/*;target/change_streams-1.0-SNAPSHOT.jar com.sapiens.changestreams.watcher.ChangeStreamConfig configNew_demo_09May.yaml