package com.ingsisbry

plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}