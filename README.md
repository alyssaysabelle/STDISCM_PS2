# Problem Set #2: Concurrent Programming w/ Dependencies

## Introduction

This Problem Set enhances the user experience by adding a dynamic 'explorer mode' to the current particle simulator, allowing interactive exploration inside the simulation environment. This option allows users to move a sprite about the canvas, making the experience more realistic. The algorithm cleverly determines and renders the particle placements in relation to the sprite's perimeter as it moves around the canvas. To guarantee a fluid and responsive simulation that captures the sprite's interactions with surrounding objects, this calls for intricate dependency management.

## Run Guide

To run the JAR file:
``` java -jar ParticleSimulator.jar ```

To compile:
``` cd src ```
``` javac ParticleSimulator.java ```
``` java ParticleSimulator ```
