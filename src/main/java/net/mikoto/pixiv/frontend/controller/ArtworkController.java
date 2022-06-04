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

/**
 * @author mikoto
 * @date 2022/6/4 1:53
 */
@Controller
public class ArtworkController {
    @Qualifier("forwardConnector")
    private final ForwardConnector forwardConnector;

    @Value("${mikoto.frontend.forward.address}")
    private String forwardAddress;

    @Value("${mikoto.frontend.forward.key}")
    private String forwardKey;

    private boolean flag = true;

    @Autowired
    public ArtworkController(@NotNull ForwardConnector forwardConnector) {
        this.forwardConnector = forwardConnector;
    }

    @GetMapping("/artwork")
    public String getArtwork(Model model, int artworkId) throws Exception {
        if (flag) {
            forwardConnector.addForwardServer(new ForwardServer(forwardAddress, 1, forwardKey));
            flag = false;
        }
        Artwork artwork = forwardConnector.getArtworkById(artworkId);

        model.addAttribute("title", "Mikoto-Pixiv-" + artwork.getArtworkTitle());

        StringBuilder image = new StringBuilder();
        for (int i = 0; i < artwork.getPageCount(); i++) {
            String imageUrl = artwork.getIllustUrlRegular().replace(artwork.getArtworkId() + "_p0", artwork.getArtworkId() + "_p" + i);
            image.append("<img src=\"")
                    .append(forwardAddress)
                    .append("/artwork/getImage?key=")
                    .append(forwardKey)
                    .append("&url=")
                    .append(imageUrl)
                    .append("\" alt=\"ArtworkImage\">");
        }
        model.addAttribute("image", image);
        model.addAttribute("artworkTitle", artwork.getArtworkTitle());
        model.addAttribute("description", artwork.getDescription());
        model.addAttribute("bookmarkCount", artwork.getBookmarkCount());
        model.addAttribute("likeCount", artwork.getLikeCount());
        model.addAttribute("viewCount", artwork.getViewCount());

        return "artwork";
    }
}
