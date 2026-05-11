package com.sos.pingreport.client;

import com.sos.pingreport.dto.ReportDTO;
import com.sos.pingreport.exception.PythonServiceException;
import com.sos.pingreport.util.AppConstantsHelper;
import com.sos.pingreport.util.ProjectResolverHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PythonClient {

    private final WebClient webClient;
    private final ProjectResolverHelper projectResolverHelper;

    // process file
    @SuppressWarnings("null")
    public List<ReportDTO> processFile(String project, MultipartFile file, String mode) {

        try {
            String url = projectResolverHelper.resolvePythonUrl(project);
            LinkedMultiValueMap<String, Object> body =
                    buildMultipartBody(file, project, mode);
            log.info("Calling Python API [{}] -> {}", project, url);
            List<ReportDTO> response = webClient.post().uri(url)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .bodyValue(body)
                            .retrieve()
                            .bodyToFlux(ReportDTO.class)
                            .collectList()
                            .block();

            if (response == null || response.isEmpty()) {
                throw new PythonServiceException("No data returned from Python service");
            }
            return response;

        } catch (Exception ex) {
            log.error("Python API call failed", ex);
            throw new PythonServiceException("Failed to call Python service");
        }
    }


    // build multipart body
    @SuppressWarnings("null")
    private LinkedMultiValueMap<String, Object> buildMultipartBody(MultipartFile file, String project, String mode) {

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(getBytes(file)) {
                    @Override
                    public String getFilename() {return file.getOriginalFilename();
                    }
                }
        );

        // D3 supports lite/full
        if (AppConstantsHelper.D3.equalsIgnoreCase(project)) {
            body.add("mode", mode);
        }
        return body;
    }

        // file bytes
        private byte[] getBytes(MultipartFile file) {

        try {
            return file.getBytes();
        } catch (Exception ex) {
            throw new PythonServiceException("Failed to read uploaded file");
        }
    }
}