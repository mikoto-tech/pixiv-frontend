package net.mikoto.pixiv.frontend.controller;

import net.mikoto.pixiv.api.model.Artwork;
import net.mikoto.pixiv.api.model.ForwardServer;
import net.mikoto.pixiv.forward.connector.ForwardConnector;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author mikoto
 * @date 2022/6/4 1:53
 */
@Controller
public class ArtworkController {
    @Qualifier("forwardConnector")
    private final ForwardConnector forwardConnector;
    @Value("${mikoto.pixiv.frontend.forwardServers}")
    private String forwardServers;

    @Autowired
    public ArtworkController(@NotNull ForwardConnector forwardConnector) {
        this.forwardConnector = forwardConnector;
    }

    @GetMapping("/artwork/{id}")
    public String getArtwork(Model model, @PathVariable(name = "id") int artworkId) throws Exception {
        if (forwardConnector.isEmpty()) {
            for (String forwardServer :
                    forwardServers.split(";")) {
                String[] forwardServerConfig = forwardServer.split(",");
                forwardConnector.addForwardServer(new ForwardServer(forwardServerConfig[0], Integer.parseInt(forwardServerConfig[1]), forwardServerConfig[2]));
            }
        }

        Artwork artwork = forwardConnector.getArtworkInformation(artworkId);

        model.addAttribute("title", "Mikoto-Pixiv-" + artwork.getArtworkTitle());

        StringBuilder image = new StringBuilder();
        for (int i = 0; i < artwork.getPageCount(); i++) {
            ForwardServer forwardServer = forwardConnector.getForwardServer();
            String imageUrl = artwork.getIllustUrlRegular().replace("https://i.pximg.net", "").replace(artwork.getArtworkId() + "_p0", artwork.getArtworkId() + "_p" + i);
            image.append("<img style=\"margin: 10px\" src=\"")
                    .append(forwardServer.getAddress())
                    .append("/artwork/getImage?key=")
                    .append(forwardServer.getKey())
                    .append("&url=")
                    .append(imageUrl)
                    .append("\" alt=\"ArtworkImage\">");
        }
        model.addAttribute("image", image);
        model.addAttribute("artwork", artwork);
        return "artwork";
    }
}
