package rip.bolt.nerve;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.utils.MapUtils;
import rip.bolt.nerve.config.AppData;

public class PrivateServerRequester {

    private static Map<String, Object> generateTemplate() {
        try {
            File dataFolder = NervePlugin.getInstance().getDataFolder();
            File templatePath = new File(dataFolder, "template.yml");
            File secretsPath = new File(dataFolder, "secrets.yml");

            Map<String, Object> template = (Map<String, Object>) new Yaml().load(new FileInputStream(templatePath));
            Map<String, Object> secrets = null;

            if (secretsPath.exists())
                secrets = (Map<String, Object>) new Yaml().load(new FileInputStream(secretsPath));
            else
                secrets = new HashMap<String, Object>();

            return (Map<String, Object>) MapUtils.merge(template, secrets);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean request(String name) {
        try {
            KubernetesClient client = new DefaultKubernetesClient();

            CustomResourceDefinitionContext context = new CustomResourceDefinitionContext.Builder()
                    .withName("helmcharts.helm.cattle.io").withGroup("helm.cattle.io").withScope("Namespaced")
                    .withVersion("v1").withPlural("helmcharts").build();

            Map<String, Object> template = generateTemplate();
            JSONObject helmChartJSONObject = new JSONObject(template);

            JSONObject metadata = helmChartJSONObject.getJSONObject("metadata");
            JSONObject spec = helmChartJSONObject.getJSONObject("spec");
            JSONObject setValues = spec.getJSONObject("set");

            metadata.put("name", "private-" + name.toLowerCase().replaceAll("_", "-") + "-server");
            spec.put("repo", AppData.PrivateServerConfig.getHelmRepoUrl());
            setValues.put("luckperms.data.password", AppData.PrivateServerConfig.getLuckpermsPassword());
            setValues.put("luckperms.data.address", AppData.PrivateServerConfig.getLuckpermsAddress());
            setValues.put("config.serverName", name);
            setValues.put("config.operators", name);

            client.customResource(context).create("minecraft", helmChartJSONObject.toString());
            client.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static boolean status(ProxiedPlayer requester) {
        return true;
    }

}
