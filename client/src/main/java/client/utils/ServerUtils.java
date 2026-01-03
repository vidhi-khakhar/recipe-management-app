/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import java.net.ConnectException;

import org.glassfish.jersey.client.ClientConfig;

import client.config.ConfigService;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {
	// The server url from the config.json
    private static final String server = ConfigService.getConfig().getServerUrl(); 

	/**
	 * Checks if the server is available by sending a simple GET request.
	 * @return true if the server responds, false if there is a connection error
	 * @throws ProcessingException for other types of network errors
	 */
    public boolean isServerAvailable() {
        try {
            ClientBuilder.newClient(new ClientConfig()) //
                    .target(server) //
                    .request(APPLICATION_JSON) //
                    .get();
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return false;
            }
        }
        return true;
    }
}
