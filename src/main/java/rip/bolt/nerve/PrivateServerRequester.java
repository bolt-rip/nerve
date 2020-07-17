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

            CustomResourceDefinitionContext context = new CustomResourceDefinitionContext.Builder().withName("helmreleases.helm.fluxcd.io").withGroup("helm.fluxcd.io").withScope("Namespaced").withVersion("v1").withPlural("helmreleases").build();

            Map<String, Object> template = generateTemplate();
            JSONObject helmReleaseJSONObject = new JSONObject(template);

            // it is needed to change it twice because without that the server name would be "minecraft-${name}"
            // more info about that in https://docs.fluxcd.io/projects/helm-operator/en/stable/references/helmrelease-custom-resource/
            JSONObject metadata = helmReleaseJSONObject.getJSONObject("metadata");
            JSONObject spec = helmReleaseJSONObject.getJSONObject("spec");
            JSONObject config = spec.getJSONObject("values").getJSONObject("config");

            metadata.put("name", "private-" + name.toLowerCase().replaceAll("_", "-") + "-server");
            spec.put("releaseName", "private-" + name.toLowerCase().replaceAll("_", "-") + "-server");
            config.put("serverName", name);
            config.put("operators", name);

            client.customResource(context).create("minecraft", helmReleaseJSONObject.toString());
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
