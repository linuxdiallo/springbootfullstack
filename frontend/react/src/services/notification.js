import { createStandaloneToast } from '@chakra-ui/react'

const { toast } = createStandaloneToast()

const notificaton = (title, description, status) => {
    toast({
        title,
        description,
        position:'top-right',
        status,
        isClosable : true,
        duration: 4000
    });
}

export const successNotification = (title, description) => {
    notificaton(title, description, "success");
}

export const errorNotification = (title, description) => {
    notificaton(title, description, "error");
}