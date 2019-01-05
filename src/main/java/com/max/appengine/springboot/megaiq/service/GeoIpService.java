/*
 * Copyright 2018 mega-iq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.max.appengine.springboot.megaiq.service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;

@Service
public class GeoIpService {
  public static final String GEOIP_DATABASE_PATH = "GeoIP2-City.mmdb";

  private DatabaseReader reader;

  private File fileDb;

  private final StorageService storageService;

  @Autowired
  public GeoIpService(StorageService storageService) throws IOException {
    this.storageService = storageService;

    initGeoIpDatabase();
  }

  public Optional<String> getLocationFromIp(String ip) {
    if (this.reader == null) {
      return Optional.empty();
    }

    try {
      InetAddress ipAddress = InetAddress.getByName(ip);
      CityResponse response = this.reader.city(ipAddress);
      Country country = response.getCountry();
      City city = response.getCity();
      String location;

      if (city == null) {
        location = country.getName();
      } else {
        location = city.getName() + ", " + country.getName();
      }

      return Optional.of(location);
    } catch (GeoIp2Exception | IOException error) {
      return Optional.empty();
    }
  }

  public String getDatabaseReaderStatus() {
    if (this.fileDb == null) {
      return "GeoIp reader is null";
    } else {
      return "GeoIp reader: " + this.reader;
    }
  }

  public String getDatabaseFileStatus() {
    if (this.fileDb == null) {
      return "GeoIp file is null";
    } else {
      return "GeoIp file: " + this.fileDb.getAbsolutePath() + " size: " + this.fileDb.length();
    }
  }
  
  public void initGeoIpDatabase() throws IOException {
    this.fileDb = createDatabaseFile();

    if (this.fileDb != null) {
      this.reader = createDatabaseReader();
    }
  }

  private File createDatabaseFile() throws IOException {
    File dbFile = null;
    try {
      dbFile = File.createTempFile("geoip-", ".tmp");
      this.storageService.fetchFile(GEOIP_DATABASE_PATH, dbFile);
    } catch (IOException e) {
      throw e;
    }
    return dbFile;
  }

  private DatabaseReader createDatabaseReader() {
    try {
      return new DatabaseReader.Builder(this.fileDb).build();
    } catch (IOException e) {
      return null;
    }
  }
}
