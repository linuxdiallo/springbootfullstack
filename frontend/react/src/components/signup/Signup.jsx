import {Flex, Heading, Image, Link, Stack, Text} from "@chakra-ui/react";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";

const Signup = () => {

    const {setCustomerFromToken} = useAuth();
    const navigate = useNavigate();

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Flex p={4} alignItems={"center"} justifyContent={"center"} flexDirection={"column"}>
                        <Image
                            borderRadius='full'
                            src='https://user-images.githubusercontent.com/40702606/210880158-e7d698c2-b19a-4057-b415-09f48a746753.png'
                            boxSize={"90px"}
                            alt={"Amigoscode Logo"}
                            m={4}
                        />
                        <Heading fontSize={'2xl'} mb={4}>Register for an new account</Heading>
                    </Flex>
                    <CreateCustomerForm onSuccess={(token) => {
                        localStorage.setItem("access_token", token);
                        setCustomerFromToken();
                        navigate("/dashboard")
                    }}/>
                    <Link color={"blue.500"} href={"/"}>
                        Have an account? Sign in now.
                    </Link>
                </Stack>
            </Flex>
            <Flex
                flex={1}
                p={10}
                flexDirection={'column'}
                alignItems={'center'}
                justifyContent={'center'}
                bgGradient={{
                    sm: 'linear(to-r, blue.600, purple.600)'
                }}
            >
                <Text fontSize={'6xl'}
                      color={'white'}
                      fontWeight={'bold'}
                      m={'5'}>
                    <Link href={"https://amigoscode.com/courses"}>
                        Enrol Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Signup;