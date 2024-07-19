package com.example.CiryllicToLatin.Service;

import com.example.CiryllicToLatin.DTO.ClientDto;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilesService {
    @Autowired
    private CiryllicToLatynService ciryllicToLatynService;
    private final String uploadDir = "files/";
    public void saveFile(MultipartFile file) throws IOException {
        try {
            String uploadDir = "files/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = file.getInputStream()) {

                Files.copy(inputStream, Paths.get(uploadDir + file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            System.out.println("Ошибка: " + exception.getMessage());
        }
    }

    public void writeIntoTxt(List<ClientDto> convertedClients){
        try {

            File file = new File("files//123.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (ClientDto clientDto : convertedClients) {
                bw.write(clientDto.getFullName());
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void download(HttpServletResponse response) throws IOException {
        File file = new File("files//123.txt");
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", file.getName());

        response.setHeader(headerKey, headerValue);

        ServletOutputStream outputStream = response.getOutputStream();

        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();
    }

    public List<ClientDto> test(List<ClientDto> clients){
        List<ClientDto> clientsOutput = new ArrayList<>();
        try{
            for(ClientDto clientDto : clients){
                String clientConvertedFIO = ciryllicToLatynService.convertCyrilic(clientDto.getFullName());
                ClientDto client = new ClientDto();
                client.setFullName(clientConvertedFIO);
                clientsOutput.add(client);
            }
            return clientsOutput;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}