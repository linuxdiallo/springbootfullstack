package com.codepro.exception;

import java.time.LocalDateTime;

/**
 * @author aboubacar.diallo
 * @created 05/09/2023 - 14:53
 * @project springbootfullstack
 * @package com.codepro.exception
 */

public record ApiError(
        String path,
        String message,
        int statusCode,
        String localDateTime
) {
}
