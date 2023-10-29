import {Link, useNavigate} from "react-router-dom";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import {useState} from "react";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import "../styles/auth.css";
import {AuthContext} from "../context/AuthContext";
import {useContext} from "react";
import instance from "../api/axios";
import {BarLoader} from "react-spinners";

const SignUpPage = () => {
    const navigate = useNavigate();
    const {isLoading, setIsLoading} = useContext(AuthContext);
    const [showPassword, setShowPassword] = useState(false);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (username === "" || password === "" || email === "") {
            return;
        }
        setIsLoading(true);
        const res = await instance.post("/api/auth/signup", {username, email, password});
        setIsLoading(false);
        setEmail("");
        setPassword("");
        setUsername("");
        if (!isLoading && res.status === 201) {
            navigate("/login");
        }
    };

    const toggleShowPassword = () => {
        setShowPassword(!showPassword);
    };

    return (
        <section className='auth'>
            <div className='wrapper'>
                <button className='back-btn' onClick={() => navigate(-1)}>
                    <ArrowBackIcon className='back-icon' />
                </button>
                <div className='banner'>
                    <div className='circle'></div>
                    <div className='overlay'></div>
                </div>
                <div className='auth-body'>
                    <form className='auth-form' onSubmit={handleSubmit}>
                        <h3 className='auth-title'>Welcome user</h3>
                        <p className='auth-desc'>Hello there! Register by entering details</p>
                        <div className='input-group'>
                            <label htmlFor='username' className='label'>
                                Username
                            </label>
                            <div className='input-container' data-error='enter valid email'>
                                <input
                                    type='text'
                                    name='username'
                                    placeholder='Enter username'
                                    autoFocus='on'
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    id='username'
                                />
                            </div>
                        </div>
                        <div className='input-group'>
                            <label htmlFor='email' className='label'>
                                Email
                            </label>
                            <div className='input-container' data-error='enter valid email'>
                                <input
                                    type='email'
                                    name='email'
                                    required
                                    pattern='/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/g'
                                    placeholder='Enter email'
                                    autoComplete='username'
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    id='email'
                                />
                            </div>
                        </div>
                        <div className='input-group'>
                            <label htmlFor='password' className='label'>
                                Password
                            </label>
                            <div className='input-container'>
                                <input
                                    type={showPassword ? "text" : "password"}
                                    name='password'
                                    required
                                    placeholder='Enter password'
                                    min='6'
                                    autoComplete='new-password'
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    id='new-password'
                                />
                                {!showPassword ? (
                                    <VisibilityOffIcon className='pwd-icon' onClick={toggleShowPassword} />
                                ) : (
                                    <VisibilityIcon className='pwd-icon' onClick={toggleShowPassword} />
                                )}
                            </div>
                        </div>
                        <button className='auth-btn' type='submit'>
                            {isLoading ? (
                                <div className='loader'>
                                    <BarLoader color='#fff' loading={isLoading} size={10} height={2} />
                                </div>
                            ) : (
                                "Sign up"
                            )}
                        </button>
                        <span className='message'>
                            Already have an account?
                            <Link to='/login' className='link-to'>
                                Sign in
                            </Link>
                        </span>
                    </form>
                </div>
            </div>
        </section>
    );
};

export default SignUpPage;
