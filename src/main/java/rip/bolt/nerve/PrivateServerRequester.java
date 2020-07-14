package rip.bolt.nerve;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.json.JSONObject;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PrivateServerRequester {

    public static boolean request(String name) {
        try {
            KubernetesClient client = new DefaultKubernetesClient();

            CustomResourceDefinitionContext context = new CustomResourceDefinitionContext.Builder().withName("helmreleases.helm.fluxcd.io").withGroup("helm.fluxcd.io").withScope("Namespaced").withVersion("v1").withPlural("helmreleases").build();

            Map<String, Object> template = client.customResource(context).load(new FileInputStream(new File(NervePlugin.getInstance().getDataFolder(), "template.yml")));
            JSONObject helmReleaseJSONObject = new JSONObject(template);

            // it is needed to change it twice because without that the server name would be "minecraft-${name}"
            // more info about that in https://docs.fluxcd.io/projects/helm-operator/en/stable/references/helmrelease-custom-resource/
            JSONObject metadata = helmReleaseJSONObject.getJSONObject("metadata");
            JSONObject spec = helmReleaseJSONObject.getJSONObject("spec");
            JSONObject config = spec.getJSONObject("values").getJSONObject("config");

            metadata.put("name", name);
            spec.put("releaseName", name);
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
