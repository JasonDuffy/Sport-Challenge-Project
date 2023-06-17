import { useLocation, useNavigate, useParams } from 'react-router-dom';

function withRouter(Component) {
  	function ComponentWithRouterProp(props) {
		let navigate = useNavigate();
    	let location = useLocation();
    	let params = useParams();

    	return (
      		<Component
        		{...props}
        		location={location}
        		params={params}
        		navigate={navigate}
      		/>
    	);
  	}

	
  	return ComponentWithRouterProp;
}

export default withRouter;