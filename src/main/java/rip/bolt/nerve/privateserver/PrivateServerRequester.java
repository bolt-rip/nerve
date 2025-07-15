package rip.bolt.nerve.privateserver;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import rip.bolt.nerve.utils.MapUtils;

public class PrivateServerRequester {

    protected File dataFolder;

    @Inject
    public PrivateServerRequester(@DataDirectory Path dataFolder) {
        this.dataFolder = dataFolder.toFile();
    }

    private Map<String, Object> generateTemplate() {
        try {
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

    public boolean request(String name) {
        try {
            KubernetesClient client = new DefaultKubernetesClient();

            CustomResourceDefinitionContext context = new CustomResourceDefinitionContext.Builder().withName("helmcharts.helm.cattle.io").withGroup("helm.cattle.io").withScope("Namespaced").withVersion("v1").withPlural("helmcharts").build();

            Map<String, Object> template = generateTemplate();
            JSONObject helmChartJSONObject = new JSONObject(template);

            JSONObject metadata = helmChartJSONObject.getJSONObject("metadata");
            JSONObject spec = helmChartJSONObject.getJSONObject("spec");
            JSONObject setValues = spec.getJSONObject("set");

            metadata.put("name", "private-" + name.toLowerCase().replaceAll("_", "-") + "-server");
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

    public boolean exists(Player requester) {
        try {
            KubernetesClient client = new DefaultKubernetesClient();

            CustomResourceDefinitionContext context = new CustomResourceDefinitionContext.Builder().withName("helmcharts.helm.cattle.io").withGroup("helm.cattle.io").withScope("Namespaced").withVersion("v1").withPlural("helmcharts").build();

            Map<String, Object> helmCharts = client.customResource(context).list("minecraft");
            client.close();

            String k8sName = "private-" + requester.getUsername().toLowerCase().replaceAll("_", "-") + "-server";
            return helmCharts.toString().contains(k8sName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
