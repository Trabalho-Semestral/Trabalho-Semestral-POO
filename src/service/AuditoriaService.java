package service;

import java.util.*;
import java.text.SimpleDateFormat;
import java.io.*;

public class AuditoriaService {
    private static final String LOG_FILE = "data/auditoria_login.ndjson";
    
    public AuditoriaService() {
        new File("data").mkdirs();
    }
    
    public void registrarLogin(String usuarioId, String tipoUsuario, String ip) {
        registrarEvento(usuarioId, tipoUsuario, "LOGIN", ip);
    }
    
    public void registrarLogout(String usuarioId, String tipoUsuario, String ip) {
        registrarEvento(usuarioId, tipoUsuario, "LOGOUT", ip);
    }
    
    private void registrarEvento(String usuarioId, String tipoUsuario, String acao, String ip) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            Map<String, String> logEntry = new HashMap<>();
            logEntry.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            logEntry.put("usuarioId", usuarioId);
            logEntry.put("tipoUsuario", tipoUsuario);
            logEntry.put("acao", acao);
            logEntry.put("ip", ip);

            out.println(new com.google.gson.Gson().toJson(logEntry));
            
        } catch (IOException e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
        }
    }
    
    public List<String[]> getLogsRecentes() {
        List<String[]> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Map<?, ?> logEntry = new com.google.gson.Gson().fromJson(line, Map.class);
                        String dataHora = sdf.format(
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) logEntry.get("timestamp"))
                        );
                        logs.add(new String[]{
                            dataHora,
                            (String) logEntry.get("usuarioId"),
                            (String) logEntry.get("tipoUsuario"),
                            (String) logEntry.get("acao"),
                            (String) logEntry.get("ip")
                        });
                    } catch (Exception e) {

                    }
                }
            }
        } catch (IOException e) {
        }
        
        // Ordenar por data mais recente primeiro
        logs.sort((a, b) -> b[0].compareTo(a[0]));
        return logs.subList(0, Math.min(logs.size(), 100));
    }
}