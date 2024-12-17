package lsit.Repositories;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lsit.Models.Client;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ClientRepository {

    final String BUCKET = "clothes-bucket";  // Your GCP bucket name
    final String CLIENTS_FOLDER = "clients/";  // Folder in the bucket for client data

    Storage storage;

    public ClientRepository() {
        // Initialize the Google Cloud Storage client
        storage = StorageOptions.getDefaultInstance().getService();
    }

    // Add a new client object to Google Cloud Storage
    public void addClient(Client client) {
        try {
            client.setId(UUID.randomUUID());  // Assign a unique ID to the client

            ObjectMapper om = new ObjectMapper();
            String clientJson = om.writeValueAsString(client);  // Convert client to JSON

            // Create a BlobId (unique identifier for the object in GCP Storage)
            BlobId blobId = BlobId.of(BUCKET, CLIENTS_FOLDER + client.getId().toString() + ".json");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();

            // Upload the client as a JSON object in GCP Storage
            storage.create(blobInfo, clientJson.getBytes());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Get a client by its UUID
    public Client getClient(UUID id) {
        try {
            // Retrieve the client from Google Cloud Storage using its UUID
            Blob blob = storage.get(BlobId.of(BUCKET, CLIENTS_FOLDER + id.toString() + ".json"));

            if (blob == null) {
                return null;  // If the file does not exist
            }

            // Read the content of the blob (client data)
            byte[] content = blob.getContent();

            // Convert the byte array back into a Client object
            ObjectMapper om = new ObjectMapper();
            return om.readValue(content, Client.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Remove a client by its UUID
    public void removeClient(UUID id) {
        // Delete the client from Google Cloud Storage
        storage.delete(BlobId.of(BUCKET, CLIENTS_FOLDER + id.toString() + ".json"));
    }

    // Update an existing client object
    public void updateClient(Client client) {
        try {
            // Retrieve the existing client first
            Client existingClient = this.getClient(client.getId());
            if (existingClient == null) return;  // If the client does not exist, don't update

            // Convert the updated client to JSON
            ObjectMapper om = new ObjectMapper();
            String clientJson = om.writeValueAsString(client);

            // Create or overwrite the client in Google Cloud Storage
            BlobId blobId = BlobId.of(BUCKET, CLIENTS_FOLDER + client.getId().toString() + ".json");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();
            storage.create(blobInfo, clientJson.getBytes());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // List all clients
    public List<Client> listClients() {
        List<Client> clients = new ArrayList<>();

        // List objects (client files) under the specified prefix (folder)
        Iterable<Blob> objects = storage.list(BUCKET, Storage.BlobListOption.prefix(CLIENTS_FOLDER)).iterateAll();

        // For each object (client file), retrieve the content and map it to a Client object
        for (Blob object : objects) {
            try {
                byte[] content = object.getContent();
                ObjectMapper om = new ObjectMapper();
                Client client = om.readValue(content, Client.class);
                clients.add(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return clients;
    }
}
