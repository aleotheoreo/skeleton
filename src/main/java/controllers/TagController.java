package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import api.TagResponse;
import dao.ReceiptDao;
import dao.TagDao;
import generated.tables.records.TagsRecord;
import generated.tables.records.ReceiptsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagController {
    final TagDao tags;
    final ReceiptDao receipts;

    public TagController(ReceiptDao receipts, TagDao tags) {
        this.receipts = receipts;
        this.tags = tags;
    }

    @PUT
    @Path("/tags/{tag}")
    public Response toggleTag(@NotNull @PathParam("tag") String tagName, Integer receiptID) {
        // make sure receiptID is valid (return 404 error otherwise)
        if (! receipts.isValidReceiptID(receiptID)) {
            throw new WebApplicationException(String.format("Invalid reference to Receipt ID %d", receiptID), Response.Status.NOT_FOUND);
        }
        // remove tag if receipt already has the tag
        if (tags.isReceiptTagged(receiptID, tagName)) {
            tags.untagReceipt(receiptID, tagName);
        }
        // otherwise add tag to receipt
        else {
            tags.tagReceipt(receiptID, tagName);
        }
        return Response.ok().build();
    }


    @GET
    @Path("/tags/{tag}")
    public List<ReceiptResponse> getReceiptsFromTag(@NotNull @PathParam("tag") String tagName) {
        // get list of receipt ID's from TAG table
        List<Integer> receipt_ids = tags.getReceiptIDsFromTag(tagName);

        // get list of receipt responses
        List<ReceiptsRecord> receiptRecords = new ArrayList<>();
        for (int receipt_id : receipt_ids) {
            receiptRecords.add(receipts.getReceipt(receipt_id));
        }

        // return stream of receipt responses
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }

    @GET
    @Path("/tags")
    public List<TagResponse> getTags() {
        List<TagsRecord> tagsRecords = tags.getAllTags();

        // get list of tags from TAG table
        return tagsRecords.stream().map(TagResponse::new).collect(toList());

    }
}
