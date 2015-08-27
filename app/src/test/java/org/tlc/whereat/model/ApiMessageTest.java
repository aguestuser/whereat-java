package org.tlc.whereat.model;


import com.google.gson.Gson;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ApiHelpers.*;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */
public class ApiMessageTest {

    // CONSTRUCTORS

    @Test
    public void of_should_constructApiMessage(){
        assertThat(
            ApiMessage.of(REMOVE_MSG))
            .isEqualTo(removeMsgStub());
    }

    @Test
    public void get_should_returnMsg(){
        assertThat(ApiMessage.of(REMOVE_MSG).get()).isEqualTo(REMOVE_MSG);
    }

    @Test
    public void toJson_should_serializeToJson(){
        assertThat(removeMsgStub().toJson()).isEqualTo(REMOVE_MSG_JSON);
    }

    @Test
    public void fromJson_should_deserializeFromJson(){
        assertThat(ApiMessage.fromJson(REMOVE_MSG_JSON)).isEqualTo(removeMsgStub());
    }

}