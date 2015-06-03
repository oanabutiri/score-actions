package io.cloudslang.content.rft.actions;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import com.hp.oo.sdk.content.annotations.Action;
import com.hp.oo.sdk.content.annotations.Output;
import com.hp.oo.sdk.content.annotations.Param;
import com.hp.oo.sdk.content.annotations.Response;
import com.hp.oo.sdk.content.plugin.ActionMetadata.MatchType;
import com.hp.oo.sdk.content.plugin.ActionMetadata.ResponseType;
import io.cloudslang.content.rft.service.RemoteCopy;
import io.cloudslang.content.rft.utils.RemoteCopyInputs;
import io.cloudslang.content.rft.utils.RemoteCopyOutputs;

/**
 * Created by butiri on 5/29/2015.
 */
public class RemoteCopyAction {

    @Action(name = "Remote Copy",
            outputs = {
                    @Output(RemoteCopyOutputs.RETURN_RESULT),
                    @Output(RemoteCopyOutputs.RETURN_CODE),
                    @Output(RemoteCopyOutputs.EXCEPTION)
            },
            responses = {
                    @Response(text = RemoteCopyOutputs.SUCCESS, field = RemoteCopyOutputs.RETURN_CODE, value = RemoteCopyOutputs.SUCCESS_RETURN_CODE, matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.RESOLVED),
                    @Response(text = RemoteCopyOutputs.FAILURE, field = RemoteCopyOutputs.RETURN_CODE, value = RemoteCopyOutputs.FAILURE_RETURN_CODE, matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.ERROR)
            }
    )
    public Map<String, String> execute(
            @Param(value = RemoteCopyInputs.SRC_HOST, required = true) String sourceHost,
            @Param(value = RemoteCopyInputs.SRC_PORT) String sourcePort,
            @Param(value = RemoteCopyInputs.SRC_USERNAME) String sourceUsername,
            @Param(value = RemoteCopyInputs.SRC_PASSWORD) String sourcePassword,
            @Param(value = RemoteCopyInputs.SRC_PRIVATE_KEY_FILE) String sourcePrivateKeyFile,
            @Param(value = RemoteCopyInputs.SRC_PATH, required = true) String sourcePath,
            @Param(value = RemoteCopyInputs.SRC_PROTOCOL, required = true) String sourceProtocol,
            @Param(value = RemoteCopyInputs.SRC_CHARACTERSET) String sourceCharacterset,
            @Param(value = RemoteCopyInputs.SRC_TIMEOUT) String sourceTimeout,
            @Param(value = RemoteCopyInputs.DEST_HOST, required = true) String destinationHost,
            @Param(value = RemoteCopyInputs.DEST_PORT) String destinationPort,
            @Param(value = RemoteCopyInputs.DEST_USERNAME) String destinationUsername,
            @Param(value = RemoteCopyInputs.DEST_PASSWORD) String destinationPassword,
            @Param(value = RemoteCopyInputs.DEST_PRIVATE_KEY_FILE) String destinationPrivateKeyFile,
            @Param(value = RemoteCopyInputs.DEST_PATH, required = true) String destinationPath,
            @Param(value = RemoteCopyInputs.DEST_PROTOCOL, required = true) String destinationProtocol,
            @Param(value = RemoteCopyInputs.DEST_CHARACTERSET) String destinationCharset,
            @Param(value = RemoteCopyInputs.DEST_TIMEOUT) String destinationTimeout,
            @Param(value = RemoteCopyInputs.FILE_TYPE) String fileType,
            @Param(value = RemoteCopyInputs.PASSIVE) String passive
    ) throws Exception {
        RemoteCopyInputs remoteCopyInputs = new RemoteCopyInputs();
        remoteCopyInputs.setSourceHost(sourceHost);
        remoteCopyInputs.setSourcePort(sourcePort);
        remoteCopyInputs.setSourceUsername(sourceUsername);
        remoteCopyInputs.setSourcePassword(sourcePassword);
        remoteCopyInputs.setSourcePrivateKeyFile(sourcePrivateKeyFile);
        remoteCopyInputs.setSourcePath(sourcePath);
        remoteCopyInputs.setSourceProtocol(sourceProtocol);
        remoteCopyInputs.setSourceCharacterSet(sourceCharacterset);
        remoteCopyInputs.setSourceTimeout(sourceTimeout);
        remoteCopyInputs.setDestinationHost(destinationHost);
        remoteCopyInputs.setDestinationPort(destinationPort);
        remoteCopyInputs.setDestinationUsername(destinationUsername);
        remoteCopyInputs.setDestinationPassword(destinationPassword);
        remoteCopyInputs.setDestinationPrivateKeyFile(destinationPrivateKeyFile);
        remoteCopyInputs.setDestinationPath(destinationPath);
        remoteCopyInputs.setDestinationProtocol(destinationProtocol);
        remoteCopyInputs.setDestinationCharacterSet(destinationCharset);
        remoteCopyInputs.setDestinationTimeout(destinationTimeout);
        remoteCopyInputs.setFileType(fileType);
        remoteCopyInputs.setPassive(passive);

        try {
            return new RemoteCopy().execute(remoteCopyInputs);
        } catch (Exception e) {
            return exceptionResult(e.getMessage(), e);
        }
    }

    private Map<String, String> exceptionResult(String message, Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String eStr = writer.toString().replace("\u0000", "");

        Map<String, String> returnResult = new HashMap<String, String>();
        returnResult.put(RemoteCopyOutputs.RETURN_RESULT, message);
        returnResult.put(RemoteCopyOutputs.RETURN_CODE, RemoteCopyOutputs.FAILURE_RETURN_CODE);
        returnResult.put(RemoteCopyOutputs.EXCEPTION, eStr);
        return returnResult;
    }
}
